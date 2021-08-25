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
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabGroupApi gitlabGroupApi;
    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;
    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;
    @Autowired
    private MemberAuditLogRepository memberAuditLogRepository;
    @Autowired
    private IRdmMemberAuditRecordService iRdmMemberAuditRecordService;
    @Autowired
    private TransactionalProducer transactionalProducer;
    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;

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

    @Override
    public void auditFix(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMemberAuditRecord dbRecord = rdmMemberAuditRecordRepository.selectByPrimaryKey(rdmMemberAuditRecord.getId());
        AssertExtensionUtils.notNull(dbRecord, "该记录不存在");
        //数据修复分为两个层级，一个是project层，一个是group层
        if (StringUtils.equalsIgnoreCase(rdmMemberAuditRecord.getType(), AuthorityTypeEnum.GROUP.getValue())) {
            groupPermissionsRepair(dbRecord);

        } else {
           projectPermissionsRepair(dbRecord);
        }
        // <2> 修复结束, 设置审计记录同步标识为true
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
    }

    private void projectPermissionsRepair(RdmMemberAuditRecord dbRecord) {
        //项目group权限修复
        Long repositoryId = dbRecord.getRepositoryId();

        Long userId = dbRecord.getUserId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId)) {
            //如果userId不存在，这个数据就是异常的数据，那么就删除
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }
        // 若glUserId为null, 获取glUserId
        Integer glUserId = dbRecord.getGlUserId() != null ? dbRecord.getGlUserId() : c7NBaseServiceFacade.userIdToGlUserId(userId);
        if (Objects.isNull(glUserId)) {
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }
        Integer glProjectId = dbRecord.getGlProjectId();
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(dbRecord.getProjectId());
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());

        // 查询权限
        RdmMember dbMember = rdmMemberRepository.selectOneByUk(dbRecord.getProjectId(), repositoryId, userId);
        // 查询用户在组里有权限，project里面没有权限，这里查询项目的角色就是404
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
        Boolean isOrgAdmin = c7NBaseServiceFacade.checkIsOrgAdmin(dbRecord.getOrganizationId(), userId);
        logger.debug("用户[{}]是否为组织[{}]管理员[{}]", userId, dbRecord.getOrganizationId(), isOrgAdmin);

        // 判断是否是项目团队成员
        C7nUserVO c7nUserVO = c7NBaseServiceFacade.detailC7nUserOnProjectLevel(dbRecord.getProjectId(), userId);
        boolean isProjectMember = c7nUserVO != null;
        logger.debug("用户[{}]是否为项目[{}]的成员[{}]", userId, dbRecord.getProjectId(), isProjectMember);

        // <> 按gitlab group和project两类情况讨论
        // 是否为组织管理员

        if (isOrgAdmin) {
            orgAdminPermissionRepair(dbRecord, repositoryId, userId, glUserId, glProjectId, glGroupId, dbMember, groupGlMember, isProjectMember);
        } else {
            // 是否为团队成员
            if (isProjectMember) {
                projectMemberOrOwnerPermissionRepar(dbRecord, repositoryId, userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, c7nUserVO);
            } else {
                //不是团队成员
                handNonProjectMember(userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
            }
        }
    }

    private void projectMemberOrOwnerPermissionRepar(RdmMemberAuditRecord dbRecord, Long repositoryId, Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, C7nUserVO c7nUserVO) {
        // 如果是团队成员
        Boolean isProjectAdmin = c7nUserVO.isProjectAdmin();
        // 是否是项目管理员
        if (isProjectAdmin) {
            // 如果是项目管理员 修复为group Owner权限
            logger.debug("修复用户[{}]为项目管理员权限", userId);
            if (Objects.isNull(dbMember) && !Objects.isNull(groupGlMember)) {
                insertProjectMember(dbRecord, repositoryId, userId, glUserId, glProjectId);
            }
            updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);

        } else {
            //是团队成员但是不是项目所有者
            handProjectMember(userId, glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
        }
    }

    private void orgAdminPermissionRepair(RdmMemberAuditRecord dbRecord, Long repositoryId, Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member groupGlMember, boolean isProjectMember) {
        // 修复为group Owner权限
        logger.debug("修复用户[{}]为组织管理员权限", userId);
        //如果是组织管理员，又是项目成员，需要插入dbMember
        if (isProjectMember && Objects.isNull(dbMember)) {
            insertProjectMember(dbRecord, repositoryId, userId, glUserId, glProjectId);
        }
        updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
    }

    private void insertProjectMember(RdmMemberAuditRecord dbRecord, Long repositoryId, Long userId, Integer glUserId, Integer glProjectId) {
        RdmMember rdmMember = new RdmMember();
        rdmMember.setSyncGitlabFlag(true);
        rdmMember.setGlAccessLevel(AccessLevel.OWNER.toValue());
        rdmMember.setProjectId(dbRecord.getProjectId());
        rdmMember.setUserId(userId);
        rdmMember.setRepositoryId(repositoryId);
        rdmMember.setOrganizationId(dbRecord.getOrganizationId());
        rdmMember.setGlProjectId(glProjectId);
        rdmMember.setGlUserId(glUserId);
        rdmMember.setType(AuthorityTypeEnum.PROJECT.getValue());
        rdmMemberRepository.insert(rdmMember);
    }

    private void groupPermissionsRepair(RdmMemberAuditRecord dbRecord) {
        Long userId = dbRecord.getUserId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId)) {
            //如果userId不存在，这个数据就是异常的数据，那么就删除
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }

        // 获取glUserId
        Integer glUserId = dbRecord.getGlUserId() != null ? dbRecord.getGlUserId() : c7NBaseServiceFacade.userIdToGlUserId(userId);
        if (Objects.isNull(glUserId)) {
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(dbRecord.getId());
            return;
        }
        //获取项目id
        Integer glProjectId = dbRecord.getGlProjectId();
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(dbRecord.getProjectId());
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());

        //查询choerodon权限
        RdmMember record = new RdmMember();
        record.setType(AuthorityTypeEnum.GROUP.getValue());
        record.setgGroupId(dbRecord.getgGroupId());
        record.setProjectId(dbRecord.getProjectId());
        record.setUserId(dbRecord.getUserId());
        RdmMember dbMember = rdmMemberRepository.selectOne(record);

        // 查询用户在gitlab中group的权限
        Member groupGlMember = gitlabGroupFixApi.getMember(glGroupId, glUserId);
        if (Objects.nonNull(groupGlMember)) {
            logger.debug("Gl组[{}]权限，ID为[{}],用户名[{}]的权限级别[{}]", glGroupId, groupGlMember.getId(), groupGlMember.getName(), groupGlMember.getAccessLevel());
        }

        // 判断是否是组织管理员
        Boolean isOrgAdmin = c7NBaseServiceFacade.checkIsOrgAdmin(dbRecord.getOrganizationId(), userId);
        // 判断是否是项目团队成员
        C7nUserVO c7nUserVO = c7NBaseServiceFacade.detailC7nUserOnProjectLevel(dbRecord.getProjectId(), userId);
        boolean isProjectMember = c7nUserVO != null;

        //如果是组织管理员
        if (isOrgAdmin) {
            // 修复为group Owner权限
            logger.debug("修复用户[{}]为组织管理员权限", userId);
            //如果是组织管理员，又是项目成员，需要插入dbMember
            if (isProjectMember && Objects.isNull(dbMember)) {
                insertGroupMember(dbRecord, userId, glUserId, glProjectId);
            }
            updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
        } else {
            // 是否为团队成员
            if (isProjectMember) {
                // 如果是团队成员
                Boolean isProjectAdmin = c7nUserVO.isProjectAdmin();
                // 是否是项目管理员
                if (isProjectAdmin) {
                    // 如果是项目管理员 修复为group Owner权限
                    logger.debug("修复用户[{}]为项目管理员权限", userId);
                    if (Objects.isNull(dbMember) && !Objects.isNull(groupGlMember)) {
                        insertGroupMember(dbRecord, userId, glUserId, glProjectId);
                    }
                    updateGitlabGroupMemberWithOwner(groupGlMember, glGroupId, glUserId);
                } else {
                    projectMemberGroupPermissionsRepair(dbRecord, userId, glUserId, glGroupId, dbMember, groupGlMember);
                }
            } else {
                nonProjectMemberPermissionRepair(dbRecord, userId, glUserId, glGroupId, dbMember, groupGlMember);
            }
        }

    }

    private void nonProjectMemberPermissionRepair(RdmMemberAuditRecord dbRecord, Long userId, Integer glUserId, Integer glGroupId, RdmMember dbMember, Member groupGlMember) {
        //不是团队成员的处理
        if (Objects.isNull(dbMember)) {
            // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
            logger.debug("用户[{}]是项目成员也不是外部成员，没有代码库权限，移除", userId);
            if (groupGlMember != null) {
                gitlabGroupFixApi.removeMember(glGroupId, glUserId);
            }
        } else {
            //不是团队成员group member 不为null,dbMember不为null
            if (groupGlMember != null) {
                //如果是owner 直接删除，手动分配不可能分到owner
                if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
                    gitlabGroupFixApi.removeMember(glGroupId, glUserId);
                    rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                }
                gitlabGroupFixApi.removeMember(glGroupId, glUserId);
                if (dbMember.getSyncGitlabFlag() && !Objects.isNull(dbMember.getGlAccessLevel())) {
                    //上一步删除权限可以没有删掉 这里添加可能会报："should be higher than Owner inherited membership from group
                    //如果上一步删除组的权限没有删掉，这里就不给
                    gitlabGroupFixApi.addMember(glGroupId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                } else {
                    //如果同步失败， 直接删掉这条数据
                    rdmMemberRepository.deleteByPrimaryKey(dbMember);
                    //如果项目成员的角色存在也直接删掉
                    if (!Objects.isNull(groupGlMember)) {
                        gitlabGroupFixApi.removeMember(glGroupId, glUserId);
                    }
                }

            } else {
                // 不是团队成员， group member为null
                if (dbMember != null) {
                    rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
                }
            }

        }
    }

    private void projectMemberGroupPermissionsRepair(RdmMemberAuditRecord dbRecord, Long userId, Integer glUserId, Integer glGroupId, RdmMember dbMember, Member groupGlMember) {
        //如果是项目成员，数据库choerodon权限为null 或者未同步成功 直接删除gitlab组成员
        if (dbMember == null || !dbMember.getSyncGitlabFlag()) {
            // 如果dbMember为null 或者同步失败 移除gitlab权限
            logger.debug("用户[{}]为项目成员，但没有代码库权限，移除Gl权限", userId);
            gitlabGroupFixApi.removeMember(glGroupId, glUserId);
        } else {
            // 如果是项目成员，同步成功 但是权限不匹配 修改gitlab权限
            logger.debug("用户[{}]为项目成员，有代码库权限，修复Gl权限", userId);
            if (groupGlMember != null) {
                //如果组的权限是owner，直接删除，因为能手动分给group最高只有MAINTAINER
                if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
                    gitlabGroupFixApi.removeMember(glGroupId, glUserId);
                    rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                }
                //如果组的权限存在，先移除组的权限（随之项目的权限也会被移除,项目原来添加的非Owner权限一并移除）
                //remove的时候注意  一个组至少存在一个owner, 如果删除返回403则不处理
                gitlabGroupFixApi.removeMember(glGroupId, glUserId);
                //然后如果同步成功，按照choerodon来修复，并且choerodon中为其赋予了权限并且同步成功了
                if (dbMember.getSyncGitlabFlag() && !Objects.isNull(dbMember.getGlAccessLevel())) {
                    //上一步删除权限可以没有删掉 这里添加可能会报："should be higher than Owner inherited membership from group
                    gitlabGroupFixApi.addMember(glGroupId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                } else {
                    //如果同步失败， 直接删掉这条数据
                    rdmMemberRepository.deleteByPrimaryKey(dbMember);
                }
            }
        }
    }


    private void insertGroupMember(RdmMemberAuditRecord dbRecord, Long userId, Integer glUserId, Integer glProjectId) {
        RdmMember rdmMember = new RdmMember();
        rdmMember.setSyncGitlabFlag(true);
        rdmMember.setGlAccessLevel(AccessLevel.OWNER.toValue());
        rdmMember.setProjectId(dbRecord.getProjectId());
        rdmMember.setUserId(userId);
        rdmMember.setOrganizationId(dbRecord.getOrganizationId());
        rdmMember.setGlProjectId(glProjectId);
        rdmMember.setGlUserId(glUserId);
        rdmMember.setType(AuthorityTypeEnum.GROUP.getValue());
        rdmMember.setgGroupId(dbRecord.getgGroupId());
        rdmMemberRepository.insert(rdmMember);
    }


    private void handNonProjectMember(Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord dbRecord) {
        if (Objects.isNull(dbMember)) {
            // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
            logger.debug("用户[{}]是项目成员也不是外部成员，没有代码库权限，移除", userId);
            if (projectGlMember != null) {
                gitlabProjectFixApi.removeMember(glProjectId, glUserId);
            }
        } else {
            logger.debug("用户[{}]是外部成员，有代码库权限，修复GL权限", userId);
            updateGitLabPermission(glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
        }
    }

    private void handProjectMember(Long userId, Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord dbRecord) {
        //如果不是项目管理员，项目成员的角色
        if (dbMember == null || !dbMember.getSyncGitlabFlag()) {
            // 如果dbMember为null 或者同步失败 移除gitlab权限
            logger.debug("用户[{}]为项目成员，但没有代码库权限，移除Gl权限", userId);
            gitlabProjectFixApi.removeMember(glProjectId, glUserId);

        } else {
            // 修改gitlab权限
            logger.debug("用户[{}]为项目成员，有代码库权限，修复Gl权限", userId);
            updateGitLabPermission(glUserId, glProjectId, glGroupId, dbMember, projectGlMember, groupGlMember, dbRecord);
        }
    }

    private void updateGitLabPermission(Integer glUserId, Integer glProjectId, Integer glGroupId, RdmMember dbMember, Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord dbRecord) {
        if (groupGlMember != null) {
            //如果组的权限是owner，直接删除，项目成员不可能有组的owner
            if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
                gitlabProjectFixApi.removeMember(glProjectId, glUserId);
                rdmMemberRepository.deleteByPrimaryKey(dbMember.getId());
                rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                return;
            }
            //项目层级的权限只能比group的权限更高才能分配
            //然后如果同步成功，按照choerodon来修复，并且choerodon中为其赋予了权限并且同步成功了
            if (dbMember.getSyncGitlabFlag() && !Objects.isNull(dbMember.getGlAccessLevel())
                    && dbMember.getGlAccessLevel() > groupGlMember.getAccessLevel().value) {
                gitlabProjectFixApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                return;
            } else {
                //如果同步失败 或者项目层权限没有组的权限高的， 直接删掉这条数据
                rdmMemberRepository.deleteByPrimaryKey(dbMember);
                //如果项目成员的角色存在也直接删掉
                if (!Objects.isNull(projectGlMember)) {
                    gitlabProjectFixApi.removeMember(glProjectId, glUserId);
                }
            }
            //如果gitlab组的权限存在，且是同步失败的
        } else {
            //如果gitlab组的权限为null,
            if (projectGlMember != null) {
                // gitlab项目的权限不为null
                // 2 如果同步失败的用户或者权限小于50,按照gitlab的权限来修复
                //这里为项目变更权限的时候需要注意，如果数据库的用户的权限是50，这里按照gitlab的权限来修复。
                //如果用户是同步失败了的， AccessLevel为null
                if (!dbMember.getSyncGitlabFlag() || Objects.isNull(dbMember.getGlAccessLevel())) {
                    dbMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    dbMember.setSyncGitlabFlag(Boolean.TRUE);
                    rdmMemberRepository.updateByPrimaryKey(dbMember);
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                }
                //同步成功的 组里面没有角色 gitlab的AccessLevel只可能小于50  就按照choerodon来修数据 跟新时必须确保成员的权限小于owner
                if (!Objects.isNull(dbMember.getGlAccessLevel()) && dbMember.getGlAccessLevel() < 50 && projectGlMember.getAccessLevel().value.intValue() < 50) {
                    //有一些项目对应的组的id和他实际在gitlab上的组的id不一致，这里跟新会400
                    gitlabProjectFixApi.updateMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                } else {
                    dbMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    rdmMemberRepository.updateByPrimaryKey(dbMember);
                }

            } else {
                // 如果gitlab组的权限为null,gitlab项目的权限也为null
                // 如果在choerodon是同步成功的 权限小于50，则按照choerodon来修复权限
                if (dbMember.getSyncGitlabFlag() && dbMember.getGlAccessLevel() < 50) {
                    gitlabProjectFixApi.addMember(glProjectId, glUserId, dbMember.getGlAccessLevel(), dbMember.getGlExpiresAt());
                    rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(dbRecord);
                    return;
                }
                //如果在gitlab 一个权限也没有，在choerodon又是同步失败的，则直接删除这种数据
                //如果如果gitlab组的权限为null,gitlab项目的权限也为null，同步成功，且权限>50，这种数据也删除
                else {
                    rdmMemberRepository.deleteByPrimaryKey(dbMember);
                }
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
