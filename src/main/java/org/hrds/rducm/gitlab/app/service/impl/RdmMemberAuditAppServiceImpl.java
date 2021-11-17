package org.hrds.rducm.gitlab.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.payload.ProjectAuditPayload;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClient;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.ApplicantTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.feign.operate.AsgardServiceClientOperator;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.hrds.rducm.gitlab.infra.feign.vo.SagaInstanceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.CollectionUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;

/**
 * 成员权限审计应用服务默认实现
 * 包含审计相关的各个功能
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@Service
public class RdmMemberAuditAppServiceImpl implements RdmMemberAuditAppService {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberAuditAppServiceImpl.class);

    private static final String PROJECT_AUDIT_MEMBER_PERMISSION = "projectAuditMemberPermission";
    private static final String PROJECT_BATCH_FIX_MEMBER_PERMISSION = "projectBatchFixMemberPermission";

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
    private GitlabGroupFixApi gitlabGroupFixApi;
    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;
    @Autowired
    private TransactionalProducer transactionalProducer;
    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;
    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

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
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = SagaTopicCodeConstants.PROJECT_BATCH_AUDIT_FIX, description = "项目下批量修复成员代码权限", inputSchema = "{}")
    public void batchAuditFix(Long organizationId, Long projectId, Set<Long> recordIds) {
        if (CollectionUtils.isEmpty(recordIds)) {
            return;
        }
        ProjectAuditPayload projectAuditPayload = new ProjectAuditPayload();
        projectAuditPayload.setOrganizationId(organizationId);
        projectAuditPayload.setProjectId(projectId);
        projectAuditPayload.setRecordIds(recordIds);

        transactionalProducer.apply(
                StartSagaBuilder.newBuilder()
                        .withRefType(PROJECT_BATCH_FIX_MEMBER_PERMISSION)
                        .withRefId(projectId.toString())
                        .withSagaCode(SagaTopicCodeConstants.PROJECT_BATCH_AUDIT_FIX)
                        .withLevel(ResourceLevel.PROJECT)
                        .withSourceId(projectId)
                        .withPayloadAndSerialize(projectAuditPayload),
                builder -> {
                });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = SagaTopicCodeConstants.PROJECT_AUDIT_MEMBER_PERMISSION, description = "项目下审计成员代码权限", inputSchema = "{}")
    public void projectAudit(Long organizationId, Long projectId) {

        ProjectAuditPayload projectAuditPayload = new ProjectAuditPayload();
        projectAuditPayload.setOrganizationId(organizationId);
        projectAuditPayload.setProjectId(projectId);

        transactionalProducer.apply(
                StartSagaBuilder.newBuilder()
                        .withRefType(PROJECT_AUDIT_MEMBER_PERMISSION)
                        .withRefId(projectId.toString())
                        .withSagaCode(SagaTopicCodeConstants.PROJECT_AUDIT_MEMBER_PERMISSION)
                        .withLevel(ResourceLevel.PROJECT)
                        .withSourceId(projectId)
                        .withPayloadAndSerialize(projectAuditPayload),
                builder -> {
                });

    }

    @Override
    public SagaInstanceDetails projectAuditStatus(Long organizationId, Long projectId) {
        return getSagaInstanceDetails(PROJECT_AUDIT_MEMBER_PERMISSION, Arrays.asList(projectId.toString()), SagaTopicCodeConstants.PROJECT_AUDIT_MEMBER_PERMISSION);
    }

    @Override
    public SagaInstanceDetails projectAuditFixStatus(Long organizationId, Long projectId) {
        return getSagaInstanceDetails(PROJECT_BATCH_FIX_MEMBER_PERMISSION, Arrays.asList(projectId.toString()), SagaTopicCodeConstants.PROJECT_BATCH_AUDIT_FIX);
    }

    private SagaInstanceDetails getSagaInstanceDetails(String refType, List<String> refIds, String sagaCode) {
        List<SagaInstanceDetails> projectAuditMemberPermission = asgardServiceClientOperator.queryByRefTypeAndRefIds(refType, refIds, sagaCode);
        if (CollectionUtils.isEmpty(projectAuditMemberPermission)) {
            return new SagaInstanceDetails();
        }
        List<SagaInstanceDetails> sagaInstanceDetailsList = projectAuditMemberPermission.stream().sorted(Comparator.comparing(SagaInstanceDetails::getId).reversed()).collect(Collectors.toList());
        return sagaInstanceDetailsList.get(0);
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
