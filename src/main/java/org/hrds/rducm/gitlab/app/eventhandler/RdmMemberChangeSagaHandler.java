package org.hrds.rducm.gitlab.app.eventhandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.domain.AuditDomain;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.payload.GitlabGroupMemberVO;
import org.hrds.rducm.gitlab.app.eventhandler.payload.ProjectAuditPayload;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.entity.payload.GroupMemberPayload;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.enums.RoleLabelEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hrds.rducm.gitlab.infra.util.JsonHelper;
import org.hzero.core.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 处理用户角色变更的Saga
 * 总体逻辑:
 * 如果项目层的团队成员的角色为 项目管理员或组织管理员, 默认设置为Owner权限
 * 如果项目层的团队成员的角色为 项目成员且非组织管理员, 默认不设置权限, 需手动分配
 *
 * @author ying.xie@hand-china.com
 * @date 2020/6/8
 */
@Component
public class RdmMemberChangeSagaHandler {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberChangeSagaHandler.class);

    private static final Gson gson = new Gson();

    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;
    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;
    @Autowired
    private IRdmMemberService iRdmMemberService;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private IRdmMemberAuditRecordService iRdmMemberAuditRecordService;
    @Autowired
    private MemberAuditLogRepository memberAuditLogRepository;
    @Autowired
    private RdmMemberAuditAppService rdmMemberAuditAppService;
    @Autowired
    private GitlabGroupApi gitlabGroupApi;
    @Autowired
    private RdmMemberMapper rdmMemberMapper;


    /**
     * 角色同步事件
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_UPDATE_MEMBER_ROLE,
            description = "角色同步事件",
            sagaCode = SagaTopicCodeConstants.IAM_UPDATE_MEMBER_ROLE,
            maxRetryCount = 3, seq = 1)
    public List<GitlabGroupMemberVO> handleUpdateMemberRoleEvent(String payload) {
        final List<GitlabGroupMemberVO> gitlabGroupMemberVOList = gson.fromJson(payload,
                new TypeToken<List<GitlabGroupMemberVO>>() {
                }.getType());
        logger.info("update user role start");
        logger.info("payload:\n{}", gson.toJson(gitlabGroupMemberVOList));
        handleProjectLevel(gitlabGroupMemberVOList);
        handleOrgLevel(gitlabGroupMemberVOList);

        logger.info("update user role end");
        return gitlabGroupMemberVOList;
    }

    /**
     * 删除角色同步事件
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_DELETE_MEMBER_ROLE,
            description = "删除角色同步事件",
            sagaCode = SagaTopicCodeConstants.IAM_DELETE_MEMBER_ROLE,
            maxRetryCount = 3, seq = 1)
    public List<GitlabGroupMemberVO> handleDeleteMemberRoleEvent(String payload) {
        List<GitlabGroupMemberVO> gitlabGroupMemberVOList = gson.fromJson(payload,
                new TypeToken<List<GitlabGroupMemberVO>>() {
                }.getType());
        logger.info("delete gitlab role start");

        // 项目层
        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.PROJECT.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long projectId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();
                    Long organizationId = c7nBaseServiceFacade.getOrganizationId(projectId);
                    C7nUserVO c7nUserVO = c7nBaseServiceFacade.detailC7nUserOnProjectLevel(projectId, userId);
//                    Boolean isOrgAdmin = c7nBaseServiceFacade.checkIsOrgAdmin(organizationId, userId);
                    if (Objects.isNull(c7nUserVO)) {
                        //在这个项目下没有角色了，并且又不是组织管理员，删除应用服务的权限
                        //查询这个项目下面所有的应用服务
                        //删除项目成员的话要删除代码库的权限  如果是组织管理员，则是group的owner 这里跟他是不是组织管理员没有关系，我只去他项目的权限
                        RdmMember rdmMember = new RdmMember();
                        rdmMember.setUserId(userId);
                        rdmMember.setProjectId(projectId);
                        List<RdmMember> rdmMembers = rdmMemberRepository.select(rdmMember);
                        rdmMembers.forEach(rdmMember1 -> {
                            gitlabProjectApi.removeMember(rdmMember1.getGlProjectId(), rdmMember1.getGlUserId());
                        });
                    }
                    // 删除团队成员, 删除权限
                    handleRemoveMemberOnProjectLevel(organizationId, projectId, userId);
                });

        // 组织层
        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.ORGANIZATION.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long organizationId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();

                    // 删除组织管理员
                    if (gitlabGroupMemberVO.getRoleLabels().contains(RoleLabelEnum.TENANT_ADMIN.value())) {
                        handleRemoveOrgAdmin(organizationId, userId);
                    }
                });

        logger.info("delete gitlab role end");
        return gitlabGroupMemberVOList;
    }

    //批量添加用户权限，需要异步处理
    @SagaTask(code = SagaTaskCodeConstants.BATCH_ADD_GITLAB_MEMBER,
            description = "批量添加用户权限",
            sagaCode = SagaTopicCodeConstants.BATCH_ADD_GITLAB_MEMBER,
            maxRetryCount = 3, seq = 1)
    public void batchAddOrUpdateMembers(String payload) {
        List<RdmMember> rdmMembers = JsonHelper.unmarshalByJackson(payload, new TypeReference<List<RdmMember>>() {
        });
        if (CollectionUtils.isEmpty(rdmMembers)) {
            return;
        }
        rdmMembers.forEach((m) -> {
            // <2.1> 判断新增或更新
            boolean isExists;
            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
                isExists = false;
            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
                isExists = true;
            } else {
                throw new IllegalArgumentException("record status is invalid");
            }

            // <2.2> 新增或更新成员至gitlab
            try {
                Member glMember = iRdmMemberService.tryRemoveAndAddMemberToGitlab(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());

                // <2.3> 回写数据库
                iRdmMemberService.updateMemberAfter(m, glMember);

                // <2.4> 发送事件
                if (isExists) {
                    iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.UPDATE_MEMBER);
                } else {
                    iRdmMemberService.publishMemberEvent(m, MemberEvent.EventType.ADD_MEMBER);
                }
            } catch (Exception e) {
                // 回写数据库错误消息
                logger.error(e.getMessage(), e);
                m.setSyncGitlabErrorMsg(e.getMessage());
                rdmMemberRepository.updateOptional(m, RdmMember.FIELD_SYNC_GITLAB_ERROR_MSG);
            }
        });
    }


    @SagaTask(code = SagaTaskCodeConstants.BATCH_ADD_GROUP_MEMBERS,
            description = "项目下成员权限修复",
            sagaCode = SagaTopicCodeConstants.BATCH_ADD_GROUP_MEMBER, maxRetryCount = 3, seq = 1)
    public void addGroupMember(String payload) {
        GroupMemberPayload groupMemberPayload = JsonHelper.unmarshalByJackson(payload, GroupMemberPayload.class);

        //查询gitlab组是否存在
        Group group = gitlabGroupApi.getGroup(groupMemberPayload.getgGroupId());
        AssertUtils.notNull(group, "error.gitlab.group.not.exist", groupMemberPayload.getgGroupId());

        groupMemberPayload.getGitlabMemberCreateDTOS().forEach(gitlabMemberCreateDTO -> {
            //userId和groupId确定唯一的数据
            RdmMember record = new RdmMember();
            record.setUserId(gitlabMemberCreateDTO.getUserId());
            record.setgGroupId(groupMemberPayload.getgGroupId());
            RdmMember rdmMember = rdmMemberMapper.selectOne(record);
            if (Objects.isNull(rdmMember)) {
                throw new CommonException("error.rdmMember.is.null");
            }
            try {
                Member groupApiMember = gitlabGroupApi.getMember(groupMemberPayload.getgGroupId(), gitlabMemberCreateDTO.getgUserId());
                if (!Objects.isNull(groupApiMember)) {
                    gitlabGroupApi.removeMember(groupMemberPayload.getgGroupId(), gitlabMemberCreateDTO.getgUserId());
                }
                Member member = gitlabGroupApi.addMember(groupMemberPayload.getgGroupId(), gitlabMemberCreateDTO.getgUserId(), gitlabMemberCreateDTO.getGlAccessLevel(), gitlabMemberCreateDTO.getGlExpiresAt());
                //回写数据库
                iRdmMemberService.updateMemberAfter(rdmMember, member);
            } catch (Exception e) {
                // 回写数据库错误消息
                logger.error(e.getMessage(), e);
                record.setSyncGitlabErrorMsg(e.getMessage());
                rdmMemberRepository.updateOptional(record, RdmMember.FIELD_SYNC_GITLAB_ERROR_MSG);
            }

        });
    }


    @SagaTask(code = SagaTaskCodeConstants.PROJECT_AUDIT_MEMBER_PERMISSION,
            description = "项目下成员权限审计",
            sagaCode = SagaTopicCodeConstants.PROJECT_AUDIT_MEMBER_PERMISSION,
            maxRetryCount = 3, seq = 1)
    public void projectAudit(String payload) {
        ProjectAuditPayload projectAuditPayload = JsonHelper.unmarshalByJackson(payload, ProjectAuditPayload.class);
        // <1> 保存审计记录
        Date startDate = new Date();
        List<RdmMemberAuditRecord> records = iRdmMemberAuditRecordService.batchCompareProject(projectAuditPayload.getOrganizationId(), projectAuditPayload.getProjectId());
        Date endDate = new Date();

        //插入项目的审计日志
        String auditNo = UUID.randomUUID().toString();
        MemberAuditLog log = new MemberAuditLog();
        log.setOrganizationId(projectAuditPayload.getOrganizationId());
        log.setProjectId(projectAuditPayload.getProjectId());
        log.setAuditNo(auditNo);
        log.setAuditCount(records == null ? 0 : records.size());
        log.setAuditStartDate(startDate);
        log.setAuditEndDate(endDate);
        log.setAuditDuration(Math.toIntExact(Duration.between(startDate.toInstant(), endDate.toInstant()).toMillis()));
        memberAuditLogRepository.insertSelective(log);

    }

    @SagaTask(code = SagaTaskCodeConstants.BATCH_ADD_GITLAB_MEMBER,
            description = "项目下成员权限修复",
            sagaCode = SagaTopicCodeConstants.PROJECT_BATCH_AUDIT_FIX,
            maxRetryCount = 3, seq = 1)
    public void batchAuditFix(String payload) {
        ProjectAuditPayload projectAuditPayload = JsonHelper.unmarshalByJackson(payload, ProjectAuditPayload.class);
        Set<Long> recordIds = projectAuditPayload.getRecordIds();
        if (CollectionUtils.isEmpty(recordIds)) {
            return;
        }
        recordIds.forEach(recordId -> {
            RdmMemberAuditRecord rdmMemberAuditRecord = new RdmMemberAuditRecord();
            rdmMemberAuditRecord.setRepositoryId(0L);
            rdmMemberAuditRecord.setId(recordId);
            rdmMemberAuditRecord.setOrganizationId(projectAuditPayload.getOrganizationId());
            rdmMemberAuditRecord.setProjectId(projectAuditPayload.getProjectId());
            rdmMemberAuditAppService.auditFix(rdmMemberAuditRecord);
        });

    }


    /**
     * 处理项目层的团队成员角色变更
     * 由于saga参数只给出了修改后的角色信息, 没有给出修改前的角色信息, 无法做更精确的判断
     */
    private void handleProjectLevel(List<GitlabGroupMemberVO> gitlabGroupMemberVOList) {
        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.PROJECT.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long projectId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();
                    Long organizationId = c7nBaseServiceFacade.getOrganizationId(projectId);

                    List<String> userMemberRoleList = gitlabGroupMemberVO.getRoleLabels();
                    if (CollectionUtils.isEmpty(userMemberRoleList)) {
                        logger.info("用户角色为空, 表示删除");
                        // 无项目角色, 删除权限
                        handleRemoveMemberOnProjectLevel(organizationId, projectId, userId);
                    } else {
                        //删除的角色里面是否包含gitlab Owner的标签
                        boolean containsGitlabOwner = Boolean.FALSE;
                        if (!CollectionUtils.isEmpty(gitlabGroupMemberVO.getDeleteRoleLabels())) {
                            containsGitlabOwner = gitlabGroupMemberVO.getDeleteRoleLabels().contains(RoleLabelEnum.GITLAB_OWNER.value());
                        }
                        //新增的角色
                        String roleType = fetchProjectRoleLabel(userMemberRoleList, containsGitlabOwner);
                        RoleLabelEnum roleLabelEnum = Optional.ofNullable(EnumUtils.getEnum(RoleLabelEnum.class, roleType)).orElseThrow(IllegalArgumentException::new);

                        switch (roleLabelEnum) {
                            case PROJECT_MEMBER:
                                // 设置角色为项目成员, 删除权限
                                handleProjectMemberOnProjectLevel(organizationId, projectId, userId);
                                break;
                            case PROJECT_ADMIN:
                                // 设置角色为项目管理员, 设置默认Owner权限
                                handleProjectAdminOnProjectLevel(organizationId, projectId, userId);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    /**
     * 处理组织层的成员角色变更
     * 由于saga参数只给出了修改后的角色信息, 没有给出修改前的角色信息, 无法做更精确的判断
     * 例如: saga参数的角色为空, 无法得知是由组织管理员 -> 空, 还是组织成员 -> 空
     */
    private void handleOrgLevel(List<GitlabGroupMemberVO> gitlabGroupMemberVOList) {
        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.ORGANIZATION.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long organizationId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();

                    List<String> userMemberRoleList = gitlabGroupMemberVO.getRoleLabels();
                    if (CollectionUtils.isEmpty(userMemberRoleList)) {
                        // 如果角色为空
                        handleRemoveOrgAdmin(organizationId, userId);
                    } else {
                        String roleType = fetchOrgRoleLabel(userMemberRoleList);
                        RoleLabelEnum roleLabelEnum = Optional.ofNullable(EnumUtils.getEnum(RoleLabelEnum.class, roleType)).orElseThrow(IllegalArgumentException::new);

                        switch (roleLabelEnum) {
                            case TENANT_MEMBER:
                                // 如果角色变为组织成员
                                handleRemoveOrgAdmin(organizationId, userId);
                                break;
                            case TENANT_ADMIN:
                                // 添加组织管理员角色
                                // 1. 删除该组织下的权限
                                handleRemoveMemberOnOrgLevel(organizationId, userId);

                                // 2. 组织的所有项目中, 如果该用户在项目中, 需添加Owner权限
                                Set<Long> projectIds = c7nBaseServiceFacade.listProjectIds(organizationId);
                                projectIds.forEach(projectId -> {
                                    Map<Long, C7nUserVO> voMap = c7nBaseServiceFacade.listC7nUserToMapOnProjectLevel(projectId, Sets.newHashSet(userId));
                                    boolean isProjectMember = !voMap.isEmpty();
                                    if (isProjectMember) {
                                        // <> 插入该成员Owner权限
                                        insertProjectOwner(organizationId, projectId, userId);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    /**
     * 获取角色变更后的角色
     * 项目层:
     * - 如果是项目管理员, 返回项目管理员角色
     * - 如果是项目成员, 返回项目成员角色
     * - 如果既是项目成员又是项目管理员, 返回项目管理员角色
     * 组织层:
     * - 同理
     *
     * @param userMemberRoleList
     * @return
     */
    private String fetchProjectRoleLabel(List<String> userMemberRoleList, boolean containsGitlabOwner) {
        // 项目层  如果删除的角色不包含owner标签，则不应该去掉原来角色分配的权限
        if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_ROLE.value())) {
            if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_ADMIN.value()) && containsGitlabOwner) {
                return RoleLabelEnum.PROJECT_ADMIN.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_ADMIN.value()) && !containsGitlabOwner) {
                return RoleLabelEnum.PROJECT_ADMIN.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.GITLAB_OWNER.value()) && containsGitlabOwner) {
                return RoleLabelEnum.PROJECT_ADMIN.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.GITLAB_OWNER.value()) && !containsGitlabOwner) {
                return RoleLabelEnum.PROJECT_ADMIN.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_MEMBER.value()) && containsGitlabOwner) {
                return RoleLabelEnum.PROJECT_MEMBER.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_MEMBER.value()) && !containsGitlabOwner) {
                return RoleLabelEnum.DEFAULT.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.GITLAB_DEVELOPER.value()) && containsGitlabOwner) {
                return RoleLabelEnum.PROJECT_MEMBER.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.GITLAB_DEVELOPER.value()) && !containsGitlabOwner) {
                return RoleLabelEnum.DEFAULT.value();
            }
        }
        return null;
    }

    private String fetchOrgRoleLabel(List<String> userMemberRoleList) {

        // 组织层
//        else if (userMemberRoleList.contains(RoleLabelEnum.TENANT_ROLE.value())) {
        if (userMemberRoleList.contains(RoleLabelEnum.TENANT_ADMIN.value())) {
            return RoleLabelEnum.TENANT_ADMIN.value();
        } else if (userMemberRoleList.contains(RoleLabelEnum.TENANT_MEMBER.value())) {
            return RoleLabelEnum.TENANT_MEMBER.value();
        } else if (userMemberRoleList.contains(RoleLabelEnum.TENANT_ROLE.value())) {
            return RoleLabelEnum.TENANT_MEMBER.value();
        }
//        }

        return null;
    }


    private void handleRemoveMemberOnProjectLevel(Long organizationId, Long projectId, Long userId) {
        // 删除该成员权限
        rdmMemberRepository.deleteByProjectIdAndUserId(organizationId, projectId, userId);
    }

    private void handleProjectMemberOnProjectLevel(Long organizationId, Long projectId, Long userId) {
        // 3种情况; 无->项目成员 项目成员->项目成员 项目管理员->项目成员
        // 项目成员->项目成员 这种情况无法识别

        // 删除该成员权限
        rdmMemberRepository.deleteByProjectIdAndUserId(organizationId, projectId, userId);

        // 是否是组织管理员
        boolean isOrgAdmin = c7nBaseServiceFacade.checkIsOrgAdmin(organizationId, userId);
        if (isOrgAdmin) {
            // <> 插入该成员Owner权限
            insertProjectOwner(organizationId, projectId, userId);
        }
    }

    private void handleProjectAdminOnProjectLevel(Long organizationId, Long projectId, Long userId) {
        // <> 删除该成员权限
        rdmMemberRepository.deleteByProjectIdAndUserId(organizationId, projectId, userId);

        // <> 插入该成员Owner权限
        insertProjectOwner(organizationId, projectId, userId);
    }

    /**
     * 移除组织管理员
     */
    private void handleRemoveOrgAdmin(Long organizationId, Long userId) {
        // 1. 删除该组织下的权限
        handleRemoveMemberOnOrgLevel(organizationId, userId);

        // 2. 组织的所有项目中, 如果该用户在项目中, 并且是项目管理员, 需添加Owner权限
        Set<Long> projectIds = c7nBaseServiceFacade.listProjectIds(organizationId);
        projectIds.forEach(projectId -> {
            Map<Long, C7nUserVO> voMap = c7nBaseServiceFacade.listC7nUserToMapOnProjectLevel(projectId, Sets.newHashSet(userId));
            boolean isProjectAdmin = !voMap.isEmpty() && isProjectAdmin(voMap.get(userId));
            if (isProjectAdmin) {
                // <> 插入该成员Owner权限
                insertProjectOwner(organizationId, projectId, userId);
            }
        });
    }

    private void handleRemoveMemberOnOrgLevel(Long organizationId, Long userId) {
        // 删除该成员在整个组织的权限
        rdmMemberRepository.deleteByOrganizationIdAndUserId(organizationId, userId);
    }

    private void insertProjectOwner(Long organizationId, Long projectId, Long userId) {
        Integer glUserId = c7nBaseServiceFacade.userIdToGlUserId(userId);
        List<C7nAppServiceVO> appServiceVOS = c7nDevOpsServiceFacade.listC7nAppServiceOnProjectLevel(projectId);
        appServiceVOS.forEach(appServiceVO -> {
            if (appServiceVO.getId() == null || appServiceVO.getGitlabProjectId() == null) {
                logger.warn("项目{}, 应用服务{}, 未查询到对应GitlabProjectId, 跳过该应用服务", projectId, appServiceVO.getId());
            } else {
                Long repositoryId = appServiceVO.getId();
                Integer glProjectId = Math.toIntExact(appServiceVO.getGitlabProjectId());
                rdmMemberRepository.insertWithOwner(organizationId, projectId, repositoryId, userId, glProjectId, glUserId);
            }
        });
    }

    private Boolean isProjectAdmin(C7nUserVO vo) {
        return vo.getRoles().stream()
                .anyMatch(r -> IamRoleCodeEnum.PROJECT_OWNER.getCode().equals(r.getCode()));
    }
}
