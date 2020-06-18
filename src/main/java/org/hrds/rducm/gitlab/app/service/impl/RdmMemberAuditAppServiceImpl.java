package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 成员权限审计应用服务默认实现
 * 包含审计相关的各个功能
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@Service
public class RdmMemberAuditAppServiceImpl implements RdmMemberAuditAppService {
    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;
    @Autowired
    private IRdmMemberService iRdmMemberService;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabGroupApi gitlabGroupApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void syncByStrategy(Long id, int syncStrategy) {
        RdmMemberAuditRecord dbRecord = rdmMemberAuditRecordRepository.selectByPrimaryKey(id);
        AssertExtensionUtils.notNull(dbRecord, "该记录不存在");

        Long organizationId = dbRecord.getOrganizationId();
        Long projectId = dbRecord.getProjectId();
        Long repositoryId = dbRecord.getRepositoryId();

        // <1> 判断同步策略
        switch (syncStrategy) {
            case 1:
                // 本系统 -> Gitlab
                syncMemberToGitlabStrategy(organizationId,
                        projectId,
                        repositoryId,
                        dbRecord.getUserId(),
                        dbRecord.getGlProjectId(),
                        dbRecord.getGlUserId(),
                        dbRecord.getAccessLevel(),
                        dbRecord.getExpiresAt());
                break;
            case 2:
                // Gitlab -> 本系统
                syncMemberFromGitlabStrategy(organizationId,
                        projectId,
                        repositoryId,
                        dbRecord.getUserId(),
                        dbRecord.getGlProjectId(),
                        dbRecord.getGlUserId(),
                        dbRecord.getGlAccessLevel(),
                        dbRecord.getGlExpiresAt());
                break;
            default:
                break;
        }

        // <2> 同步结束, 设置审计记录同步标识为true
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
    }

    @Override
    public void auditFix(Long organizationId, Long projectId, Long repositoryId, Long id) {
        RdmMemberAuditRecord dbRecord = rdmMemberAuditRecordRepository.selectByUk(organizationId, projectId, repositoryId, id);
        AssertExtensionUtils.notNull(dbRecord, "该记录不存在");

        Long userId = dbRecord.getUserId();
        Integer glUserId = dbRecord.getGlUserId();
        Integer glProjectId = dbRecord.getGlProjectId();
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(projectId);
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());

        // 查询权限
        RdmMember dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId);
        // 查询gitlab权限
        Member projectGlMember = gitlabProjectApi.getMember(glProjectId, glUserId);
        Member groupGlMember = gitlabGroupApi.getMember(glGroupId, glUserId);

        // 判断是否是组织管理员
        Boolean isOrgAdmin = c7NBaseServiceFacade.checkIsOrgAdmin(organizationId, userId);

        // 判断是否是项目团队成员
        C7nUserVO c7nUserVO = c7NBaseServiceFacade.detailC7nUserOnProjectLevel(projectId, userId);
        boolean isProjectMember = c7nUserVO != null;

        // <> 按gitlab group和project两类情况讨论
        // 是否为组织管理员
        if (isOrgAdmin) {
            // 修复为group Owner权限
            updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
        } else {
            // 是否为团队成员
            if (isProjectMember) {
                // 如果是团队成员
                // 是否是项目管理员
                Boolean isProjectAdmin = c7nUserVO.isProjectAdmin();
                if (isProjectAdmin) {
                    // 修复为group Owner权限
                    updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
                } else {
                    // 修复为该用户当前的代码库权限
                    if (dbMember == null || !dbMember.getSyncGitlabFlag()) {
                        // 移除gitlab权限
                        removeGitlabMemberGP(groupGlMember, projectGlMember, glGroupId, glProjectId, glUserId);
                    } else {
                        // 修改gitlab权限
                        updateGitlabMemberP(dbMember, groupGlMember, projectGlMember, glGroupId, glProjectId, glUserId);
                    }
                }
            } else {
                // 如果不是团队成员, 移除gitlab权限
                removeGitlabMemberGP(groupGlMember, projectGlMember, glGroupId, glProjectId, glUserId);
            }
        }

        // <2> 修复结束, 设置审计记录同步标识为true
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
    }

    /**
     * 移除gitlab权限, 分两步
     * 1. 如果group有权限, 直接移除group的权限(移除group权限会将project权限也移除); 否则,进入2
     * 2. 如果project有权限, 移除project的权限
     */
    private void removeGitlabMemberGP(Member groupGlMember, Member projectGlMember, Integer glGroupId, Integer glProjectId, Integer glUserId) {
        if (groupGlMember != null) {
            gitlabGroupApi.removeMember(glGroupId, glUserId);
        } else if (projectGlMember != null) {
            gitlabProjectApi.removeMember(glProjectId, glUserId);
        }
    }

    /**
     * 更新gitlab权限, 分两步
     * 1. 如果group有权限, 先移除group的权限, 再新增(移除group权限会将project权限也移除); 否则, 进入2
     * 2. 如果project有权限, 更新project的权限; 否则, 进入3
     * 3. 如果project无权限, 新增project的权限
     */
    private void updateGitlabMemberP(RdmMember dbMember, Member groupGlMember, Member projectGlMember, Integer glGroupId, Integer glProjectId, Integer glUserId) {
        // 1
        if (groupGlMember != null) {
            gitlabGroupApi.removeMember(glGroupId, glUserId);

            gitlabProjectApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
        } else {
            if (projectGlMember != null) {
                // 2
                gitlabProjectApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
            } else {
                // 3
                gitlabProjectApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
            }
        }
    }

    /**
     * 修改Gitlab group权限为Owner, 分2步
     * 如果group成员不存在, 新增Owner
     * 如果group成员存在, 更新为Owner
     *
     * @param groupGlMember
     * @param glGroupId
     * @param glUserId
     */
    private void updateGitlabGroupMemberWithOwner(Member groupGlMember, Integer glGroupId, Integer glUserId) {
        if (groupGlMember == null) {
            // 添加
            gitlabGroupApi.addMember(glGroupId, glUserId, AccessLevel.OWNER.toValue(), null);
        } else if (!groupGlMember.getAccessLevel().toValue().equals(AccessLevel.OWNER.toValue())) {
            // 更新
            gitlabGroupApi.updateMember(glGroupId, glUserId, AccessLevel.OWNER.toValue(), null);
        }
    }


    private void syncMemberToGitlabStrategy(Long organizationId,
                                            Long projectId,
                                            Long repositoryId,
                                            Long userId,
                                            Integer glProjectId,
                                            Integer glUserId,
                                            Integer accessLevel,
                                            Date expiresAt) {
        // 本系统 -> Gitlab
        if (userId == null) {
            // 移除Gitlab成员
            iRdmMemberService.tryRemoveMemberToGitlab(glProjectId, glUserId);
        } else {
            // 若glUserId为null, 获取glUserId
            glUserId = glUserId != null ? glUserId : c7NBaseServiceFacade.userIdToGlUserId(userId);

            // 更新Gitlab成员
            iRdmMemberService.tryRemoveAndAddMemberToGitlab(glProjectId, glUserId, accessLevel, expiresAt);
        }
    }

    private void syncMemberFromGitlabStrategy(Long organizationId,
                                              Long projectId,
                                              Long repositoryId,
                                              Long userId,
                                              Integer glProjectId,
                                              Integer glUserId,
                                              Integer accessLevel,
                                              Date expiresAt) {
        // Gitlab -> 本系统
        RdmMember dbMember = new RdmMember();
        if (userId == null) {
            // 同步并新增
            // 查询Gitlab用户对应的userId
            userId = c7NDevOpsServiceFacade.glUserIdToUserId(glUserId);

            dbMember.setOrganizationId(organizationId)
                    .setProjectId(projectId)
                    .setRepositoryId(repositoryId)
                    .setUserId(userId)
                    .setGlProjectId(glProjectId)
                    .setGlUserId(glUserId)
                    .setGlAccessLevel(accessLevel)
                    .setGlExpiresAt(expiresAt)
                    .setSyncGitlabFlag(Boolean.TRUE)
                    .setSyncGitlabDate(new Date());

            // 重新插入
            rdmMemberRepository.insertSelective(dbMember);
        } else {
            // 同步并修改
            dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId);

            if (glUserId == null) {
                rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
            } else {
                dbMember.setGlAccessLevel(accessLevel)
                        .setGlExpiresAt(expiresAt)
                        .setSyncGitlabFlag(Boolean.TRUE)
                        .setSyncGitlabDate(new Date());
                rdmMemberRepository.updateByPrimaryKey(dbMember);
            }
        }

        // <> 发送事件
        iRdmMemberService.publishMemberEvent(dbMember, MemberEvent.EventType.SYNC_MEMBER);
    }
}
