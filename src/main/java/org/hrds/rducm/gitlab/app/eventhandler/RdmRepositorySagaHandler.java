package org.hrds.rducm.gitlab.app.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.payload.AppServiceImportPayload;
import org.hrds.rducm.gitlab.app.eventhandler.payload.DevOpsAppServicePayload;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.*;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 监听应用服务相关saga事件
 *
 * @author ying.xie@hand-china.com
 * @date 2020/6/15
 */
@Component
public class RdmRepositorySagaHandler {
    private static final Logger logger = LoggerFactory.getLogger(RdmRepositorySagaHandler.class);

    private static final Gson gson = new Gson();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;
    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private RdmMemberApplicantRepository rdmMemberApplicantRepository;
    @Autowired
    private MemberAuditLogRepository memberAuditLogRepository;
    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;
    @Autowired
    private RdmOperationLogRepository rdmOperationLogRepository;
    @Autowired
    private RdmMemberAppService rdmMemberAppService;

    /**
     * 创建应用服务事件后 代码库初始化权限
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_INIT_PRIVILEGE,
            description = "代码库初始化权限",
            sagaCode = SagaTopicCodeConstants.DEVOPS_CREATE_APPLICATION_SERVICE,
            maxRetryCount = 3,
            seq = 2)
    public String initPrivilegeWhenCreate(String data) {
        DevOpsAppServicePayload devOpsAppServicePayload = gson.fromJson(data, DevOpsAppServicePayload.class);

        Long projectId = devOpsAppServicePayload.getIamProjectId();
        Long organizationId = devOpsAppServicePayload.getOrganizationId();
        Long repositoryId = devOpsAppServicePayload.getAppServiceId();

        initPrivilege(organizationId, projectId, repositoryId);

        return data;
    }

    /**
     * Devops从外部代码平台导入到gitlab项目事件后, 初始化代码库权限
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_INIT_PRIVILEGE,
            description = "Devops从外部代码平台导入到gitlab项目",
            sagaCode = SagaTopicCodeConstants.DEVOPS_IMPORT_GITLAB_PROJECT,
            maxRetryCount = 3,
            seq = 2)
    public String initPrivilegeWhenImportFromGit(String data) {
        DevOpsAppServicePayload devOpsAppServicePayload = gson.fromJson(data, DevOpsAppServicePayload.class);

        Long projectId = devOpsAppServicePayload.getIamProjectId();
        Long organizationId = devOpsAppServicePayload.getOrganizationId();
        Long repositoryId = devOpsAppServicePayload.getAppServiceId();

        initPrivilege(organizationId, projectId, repositoryId);

        return data;
    }

    /**
     * devops导入内部应用服务后, 初始化代码库权限
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_INIT_PRIVILEGE,
            description = "devops导入内部应用服务",
            sagaCode = SagaTopicCodeConstants.DEVOPS_IMPORT_INTERNAL_APPLICATION_SERVICE,
            maxRetryCount = 3,
            seq = 2)
    public String initPrivilegeWhenImportInternal(String data) {
        AppServiceImportPayload appServiceImportPayload = gson.fromJson(data, AppServiceImportPayload.class);

        Long projectId = appServiceImportPayload.getProjectId();
        Long organizationId = c7nBaseServiceFacade.getOrganizationId(projectId);
        Long repositoryId = appServiceImportPayload.getAppServiceId();

        initPrivilege(organizationId, projectId, repositoryId);

        return data;
    }

    /**
     * Devops删除应用服务
     *
     * @param data
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_DELETE_PRIVILEGE,
            sagaCode = SagaTopicCodeConstants.DEVOPS_APP_DELETE,
            description = "Devops删除应用服务", maxRetryCount = 3,
            seq = 2)
    public String deletePrivilege(String data) {
        DevOpsAppServicePayload devOpsAppServicePayload;
        try {
            devOpsAppServicePayload = OBJECT_MAPPER.readValue(data, DevOpsAppServicePayload.class);
        } catch (IOException e) {
            throw new CommonException(e);
        }

        Long projectId = devOpsAppServicePayload.getIamProjectId();
        Long repositoryId = devOpsAppServicePayload.getAppServiceId();
        Long organizationId = c7nBaseServiceFacade.getOrganizationId(projectId);

        // 删除权限
        rdmMemberRepository.deleteByRepositoryId(organizationId, projectId, repositoryId);
        // 删除审计记录
        memberAuditLogRepository.deleteByRepositoryId(organizationId, projectId, repositoryId);
        rdmMemberAuditRecordRepository.deleteByRepositoryId(organizationId, projectId, repositoryId);
        // 删除操作日志
        rdmOperationLogRepository.deleteByRepositoryId(organizationId, projectId, repositoryId);
        // 删除权限申请记录
        rdmMemberApplicantRepository.deleteByRepositoryId(organizationId, projectId, repositoryId);

        logger.info("删除应用服务后情况代码库权限成功，repositoryId：{}", repositoryId);

        return data;
    }

    /**
     * Devops[停用/启用]应用服务
     *
     * @param data
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_VALID_PRIVILEGE,
            sagaCode = SagaTopicCodeConstants.DEVOPS_APP_ACTIVE,
            description = "Devops停用/启用应用服务", maxRetryCount = 3,
            seq = 1)
    public String invalidPrivilegeWhenDeactivate(String data) {
        DevOpsAppServicePayload devOpsAppServicePayload = gson.fromJson(data, DevOpsAppServicePayload.class);
        C7nAppServiceVO c7nAppServiceVO = devOpsAppServicePayload.getAppServiceDTO();
        if (Objects.isNull(c7nAppServiceVO) || Objects.isNull(c7nAppServiceVO.getActive())) {
            return data;
        }

        Long projectId = devOpsAppServicePayload.getIamProjectId();
        Long repositoryId = devOpsAppServicePayload.getAppServiceId();
        Long organizationId = c7nBaseServiceFacade.getOrganizationId(projectId);

        if (c7nAppServiceVO.getActive()) {
            rdmMemberAppService.batchValidMember(organizationId, projectId, repositoryId);
            logger.info("启用应用服务后生效代码库权限成功，repositoryId：{}", repositoryId);
        } else {
            rdmMemberAppService.batchInvalidMember(organizationId, projectId, repositoryId);
            logger.info("停用应用服务后失效代码库权限成功，repositoryId：{}", repositoryId);
        }

        return data;
    }

    /**
     * 创建应用服务有3个来源, 都需要初始化代码库权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     */
    private void initPrivilege(Long organizationId, Long projectId, Long repositoryId) {

        Integer glProjectId = c7nDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);

        // 初始化权限到代码库

        // 获取项目团队成员
        List<C7nUserVO> c7nUserVOS = c7nBaseServiceFacade.listC7nUsersOnProjectLevel(projectId);
        // 获取组织管理员
        List<C7nUserVO> orgAdmins = c7nBaseServiceFacade.listOrgAdministrator(organizationId);
        Map<Long, C7nUserVO> orgAdminsMap = orgAdmins.stream().collect(Collectors.toMap(C7nUserVO::getId, v -> v));

        // 获取需初始化的用户
        List<C7nUserVO> result = c7nUserVOS.stream()
                // 筛选出"项目管理员"或"组织管理员"用户
                .filter(vo -> {
                    // 是否是项目管理员
                    boolean isProjectAdmin = vo.getRoles().stream().anyMatch(r -> r.getCode().equals(IamRoleCodeEnum.PROJECT_OWNER.getCode()));
                    // 是否是组织管理员
                    boolean isOrgAdmin = orgAdminsMap.containsKey(vo.getId());
                    return isProjectAdmin || isOrgAdmin;
                }).collect(Collectors.toList());

        result.forEach(r -> {
            //初始化权限时，遇到GitLabId为空的用户跳过 11-06
            Integer glUserId = c7nBaseServiceFacade.userIdToGlUserId(r.getId());
            if (Objects.nonNull(glUserId)) {
                rdmMemberRepository.insertWithOwner(organizationId, projectId, repositoryId, r.getId(), glProjectId, glUserId);
            }
        });
    }
}
