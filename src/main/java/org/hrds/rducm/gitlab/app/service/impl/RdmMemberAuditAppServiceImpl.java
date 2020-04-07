package org.hrds.rducm.gitlab.app.service.impl;

import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
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
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            glUserId = glUserId != null ? glUserId : ic7nBaseServiceService.userIdToGlUserId(userId);

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
            userId = ic7nDevOpsServiceService.glUserIdToUserId(glUserId);

            dbMember.setOrganizationId(organizationId)
                    .setProjectId(projectId)
                    .setRepositoryId(repositoryId)
                    .setUserId(userId)
                    .setGlProjectId(glProjectId)
                    .setGlUserId(glUserId)
                    .setGlAccessLevel(accessLevel)
                    .setGlExpiresAt(expiresAt)
                    .setSyncGitlabFlag(Boolean.TRUE)
                    .setSyncDateGitlab(new Date());

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
                        .setSyncDateGitlab(new Date());
                rdmMemberRepository.updateByPrimaryKey(dbMember);
            }
        }

        // <> 发送事件
        iRdmMemberService.publishMemberEvent(dbMember, MemberEvent.EventType.SYNC_MEMBER);
    }
}
