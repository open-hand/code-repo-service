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
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * 成员权限审计应用服务默认实现
 * 包含审计相关的各个功能
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@Service
public class RdmMemberAuditAppServiceImpl implements RdmMemberAuditAppService {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberAuditAppServiceImpl.class);

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
    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;
    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;

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
        logger.info(">>>>{}>>>{}>>>>{}>>>>{}>", organizationId, projectId, repositoryId, id);
//         >>>>7>>>2048>>>>6227>>>>132805

        RdmMemberAuditRecord dbRecord = rdmMemberAuditRecordRepository.selectByUk(organizationId, projectId, repositoryId, id);
        AssertExtensionUtils.notNull(dbRecord, "该记录不存在");

        Long userId = dbRecord.getUserId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId)) {
            //如果userId不存在，这个数据就是异常的数据，那么就删除
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }
        // 若glUserId为null, 获取glUserId
        Integer glUserId = dbRecord.getGlUserId() != null ? dbRecord.getGlUserId() : c7NBaseServiceFacade.userIdToGlUserId(userId);
        Integer glProjectId = dbRecord.getGlProjectId();
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(projectId);
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());

        // 查询权限
        RdmMember dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId);
        // 查询用户在gitlab中project的权限
        Member projectGlMember = gitlabProjectFixApi.getMember(glProjectId, glUserId);
        if (Objects.nonNull(projectGlMember)) {
            logger.debug("Gl项目[{}]权限，ID为[{}],用户名[{}]的权限级别[{}]", glProjectId, projectGlMember.getId(), projectGlMember.getName(), projectGlMember.getAccessLevel());
        }
        // 查询用户在gitlab中group的权限
        Member groupGlMember = gitlabGroupFixApi.getMember(glGroupId, glUserId);
        if (Objects.nonNull(groupGlMember)) {
            logger.debug("Gl组[{}]权限，ID为[{}],用户名[{}]的权限级别[{}]", glGroupId, groupGlMember.getId(), groupGlMember.getName(), groupGlMember.getAccessLevel());
        }

        // 判断是否是组织管理员
        Boolean isOrgAdmin = c7NBaseServiceFacade.checkIsOrgAdmin(organizationId, userId);
        logger.debug("用户[{}]是否为组织[{}]管理员[{}]", userId, organizationId, isOrgAdmin);

        // 判断是否是项目团队成员
        C7nUserVO c7nUserVO = c7NBaseServiceFacade.detailC7nUserOnProjectLevel(projectId, userId);
        boolean isProjectMember = c7nUserVO != null;
        logger.debug("用户[{}]是否为项目[{}]的成员[{}]", userId, projectId, isProjectMember);

        // <> 按gitlab group和project两类情况讨论
        // 是否为组织管理员

        if (isOrgAdmin) {
            // 修复为group Owner权限
            logger.debug("修复用户[{}]为组织管理员权限", userId);
            updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
        } else {
            // 是否为团队成员
            if (isProjectMember) {
                // 如果是团队成员
                // 是否是项目管理员
                Boolean isProjectAdmin = c7nUserVO.isProjectAdmin();
                if (isProjectAdmin) {
                    // 如果是项目管理员 修复为group Owner权限
                    logger.debug("修复用户[{}]为项目管理员权限", userId);
                    updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);

                } else {
                    handProjectMember(userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember);
                }
            } else {
                handNonProjectMember(userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember);
            }
        }

        // <2> 修复结束, 设置审计记录同步标识为true
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
    }


    private void handNonProjectMember(Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember) {
        if (Objects.isNull(dbMember)) {
            // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
            logger.debug("用户[{}]是项目成员也不是外部成员，没有代码库权限，移除", userId);
            if (groupGlMember != null) {
                gitlabGroupFixApi.removeMember(glGroupId, glUserId);
            } else if (projectGlMember != null) {
                gitlabProjectFixApi.removeMember(glProjectId, glUserId);
            }
        } else {
            logger.debug("用户[{}]是外部成员，有代码库权限，修复GL权限", userId);
            updateGitLabPermission(glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember);
        }
    }

    private void handProjectMember(Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember) {
        //如果不是项目管理员，修复为该用户当前的代码库数据库的权限
        if (dbMember == null || !dbMember.getSyncGitlabFlag()) {
            // 移除gitlab权限
            logger.debug("用户[{}]为项目成员，但没有代码库权限，移除Gl权限", userId);
            removeGitlabMemberGP(glUserId, glProjectId, glGroupId, projectGlMember, groupGlMember);
        } else {
            // 修改gitlab权限
            logger.debug("用户[{}]为项目成员，有代码库权限，修复Gl权限", userId);
            updateGitLabPermission(glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember);
        }
    }

    private void updateGitLabPermission(Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember) {
        if (groupGlMember != null) {
            gitlabGroupFixApi.removeMember(glGroupId, glUserId);

            gitlabProjectFixApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());

        } else {
            if (projectGlMember != null) {
                // 2
                //这里为项目变更权限的时候需要注意，如果数据库的用户的权限是50，这里按照gitlab的权限来修复。
                if (dbMember.getGlAccessLevel() < 50) {
                    gitlabProjectFixApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                } else {
                    dbMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    rdmMemberRepository.updateByPrimaryKey(dbMember);
                }

            } else {
                // 3
                //这里为项目变更权限的时候需要注意，如果数据库的用户的权限是50，这里按照gitlab的权限来修复。
                if (dbMember.getGlAccessLevel() < 50) {
                    gitlabProjectFixApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                } else {
                    rdmMemberRepository.deleteByPrimaryKey(dbMember);
                }

            }
        }
    }

    private void removeGitlabMemberGP(Integer glUserId, Integer glProjectId, Integer glGroupId, Member projectGlMember, Member groupGlMember) {
        if (groupGlMember != null) {
            gitlabGroupFixApi.removeMember(glGroupId, glUserId);
        } else if (projectGlMember != null) {
            gitlabProjectFixApi.removeMember(glProjectId, glUserId);
        }
    }

    /**
     * 移除gitlab权限, 分两步
     * 1. 如果group有权限, 直接移除group的权限(移除group权限会将project权限也移除); 否则,进入2
     * 2. 如果project有权限, 移除project的权限
     */
//    private void removeGitlabMemberGP(Member groupGlMember, Member projectGlMember, Integer glGroupId, Integer glProjectId, Integer glUserId) {
//        if (groupGlMember != null) {
//            gitlabGroupApi.removeMember(glGroupId, glUserId);
//        } else if (projectGlMember != null) {
//            gitlabProjectApi.removeMember(glProjectId, glUserId);
//        }
//    }

    /**
     * 更新gitlab权限, 分两步
     * 1. 如果group有权限, 先移除group的权限, 再新增(移除group权限会将project权限也移除); 否则, 进入2
     * 2. 如果project有权限, 更新project的权限; 否则, 进入3
     * 3. 如果project无权限, 新增project的权限
     */
//    private void updateGitlabMemberP(RdmMember dbMember, Member groupGlMember, Member projectGlMember, Integer glGroupId, Integer glProjectId, Integer glUserId) {
//        // 1
//        if (groupGlMember != null) {
//            gitlabGroupApi.removeMember(glGroupId, glUserId);
//
//            gitlabProjectApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
//        } else {
//            if (projectGlMember != null) {
//                // 2
//                gitlabProjectApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
//            } else {
//                // 3
//                gitlabProjectApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
//            }
//        }
//    }

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
            gitlabGroupFixApi.addMember(glGroupId, glUserId, AccessLevel.OWNER.toValue(), null);
        } else if (!groupGlMember.getAccessLevel().toValue().equals(AccessLevel.OWNER.toValue())) {
            // 更新
            gitlabGroupFixApi.updateMember(glGroupId, glUserId, AccessLevel.OWNER.toValue(), null);
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
