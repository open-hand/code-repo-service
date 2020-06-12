package org.hrds.rducm.gitlab.app.eventhandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.iam.ResourceLevel;
import org.apache.commons.lang3.EnumUtils;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.payload.GitlabGroupMemberVO;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.enums.RoleLabelEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hzero.core.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
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

        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.PROJECT.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long projectId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();
                    Long organizationId = getOrganizationId(projectId);

                    // 删除团队成员, 删除权限
                    handleRemoveMemberOnProjectLevel(organizationId, projectId, userId);
                });

        logger.info("delete gitlab role end");
        return gitlabGroupMemberVOList;
    }

    /**
     * 处理项目层的团队成员角色变更
     */
    private void handleProjectLevel(List<GitlabGroupMemberVO> gitlabGroupMemberVOList) {
        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.PROJECT.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long projectId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();
                    Long organizationId = getOrganizationId(projectId);

                    List<String> userMemberRoleList = gitlabGroupMemberVO.getRoleLabels();
                    if (CollectionUtils.isEmpty(userMemberRoleList)) {
                        logger.info("用户角色为空, 表示删除");
                        // 无项目角色, 删除权限
                        handleRemoveMemberOnProjectLevel(organizationId, projectId, userId);
                    } else {
                        String roleType = fetchRoleLabel(userMemberRoleList);
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
     */
    private void handleOrgLevel(List<GitlabGroupMemberVO> gitlabGroupMemberVOList) {
        gitlabGroupMemberVOList.stream()
                .filter(gitlabGroupMemberVO -> gitlabGroupMemberVO.getResourceType().equals(ResourceLevel.ORGANIZATION.value()))
                .forEach(gitlabGroupMemberVO -> {
                    Long organizationId = gitlabGroupMemberVO.getResourceId();
                    Long userId = gitlabGroupMemberVO.getUserId();

                    List<String> userMemberRoleList = gitlabGroupMemberVO.getRoleLabels();
                    if (CollectionUtils.isEmpty(userMemberRoleList)) {
                        // do nothing
                    } else {
                        String roleType = fetchRoleLabel(userMemberRoleList);
                        RoleLabelEnum roleLabelEnum = Optional.ofNullable(EnumUtils.getEnum(RoleLabelEnum.class, roleType)).orElseThrow(IllegalArgumentException::new);

                        switch (roleLabelEnum) {
                            case TENANT_MEMBER:
                                // do nothing
                                break;
                            case TENANT_ADMIN:
                                // 添加组织管理员角色需删除该组织下的权限
                                handleRemoveMemberOnOrgLevel(organizationId, userId);
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
    private String fetchRoleLabel(List<String> userMemberRoleList) {
        // 项目层
        if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_ROLE.value())) {
            if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_ADMIN.value())) {
                return RoleLabelEnum.PROJECT_ADMIN.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.PROJECT_MEMBER.value())) {
                return RoleLabelEnum.PROJECT_MEMBER.value();
            }
        }
        // 组织层
        else if (userMemberRoleList.contains(RoleLabelEnum.TENANT_ROLE.value())) {
            if (userMemberRoleList.contains(RoleLabelEnum.TENANT_ADMIN.value())) {
                return RoleLabelEnum.TENANT_ADMIN.value();
            } else if (userMemberRoleList.contains(RoleLabelEnum.TENANT_MEMBER.value())) {
                return RoleLabelEnum.TENANT_MEMBER.value();
            }
        }

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
    }

    private void handleProjectAdminOnProjectLevel(Long organizationId, Long projectId, Long userId) {
        // <> 删除该成员权限
        rdmMemberRepository.deleteByProjectIdAndUserId(organizationId, projectId, userId);

        // <> 插入该成员Owner权限
        Integer glUserId = c7nBaseServiceFacade.userIdToGlUserId(userId);
        List<C7nAppServiceVO> appServiceVOS = c7nDevOpsServiceFacade.listAppServiceByActive(projectId);
        appServiceVOS.forEach(appServiceVO -> {
            Long repositoryId = appServiceVO.getId();
            Integer glProjectId = Math.toIntExact(appServiceVO.getGitlabProjectId());
            rdmMemberRepository.insertWithOwner(organizationId, projectId, repositoryId, userId, glProjectId, glUserId);
        });
    }

    private void handleRemoveMemberOnOrgLevel(Long organizationId, Long userId) {
        // 删除该成员在整个组织的权限
        rdmMemberRepository.deleteByOrganizationIdAndUserId(organizationId, userId);
    }

    private Long getOrganizationId(Long projectId) {
        C7nProjectVO c7nProjectVO = c7nBaseServiceFacade.detailC7nProject(projectId);
        Long organizationId = Optional.ofNullable(c7nProjectVO).map(C7nProjectVO::getOrganizationId).orElse(null);
        AssertUtils.notNull(organizationId, "organizationId cannot null");
        return organizationId;
    }
}
