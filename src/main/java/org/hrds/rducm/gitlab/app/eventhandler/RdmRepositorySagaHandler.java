package org.hrds.rducm.gitlab.app.eventhandler;

import com.google.gson.Gson;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTaskCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.constants.SagaTopicCodeConstants;
import org.hrds.rducm.gitlab.app.eventhandler.payload.DevOpsAppServicePayload;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/15
 */
@Component
public class RdmRepositorySagaHandler {
    private static final Logger logger = LoggerFactory.getLogger(RdmRepositorySagaHandler.class);

    private static final Gson gson = new Gson();

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;
    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    /**
     * 代码库初始化权限
     */
    @SagaTask(code = SagaTaskCodeConstants.CODE_REPO_INIT_PRIVILEGE,
            description = "代码库初始化权限",
            sagaCode = SagaTopicCodeConstants.DEVOPS_CREATE_APPLICATION_SERVICE,
            maxRetryCount = 3,
            seq = 2)
    public String initPrivilege(String data) {
        DevOpsAppServicePayload devOpsAppServicePayload = gson.fromJson(data, DevOpsAppServicePayload.class);
        Long projectId = devOpsAppServicePayload.getIamProjectId();
        Long organizationId = devOpsAppServicePayload.getOrganizationId();
        Long repositoryId = devOpsAppServicePayload.getAppServiceId();
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
            Integer glUserId = Optional.ofNullable(c7nBaseServiceFacade.userIdToGlUserId(r.getId())).orElseThrow(() -> new CommonException("error.glUserId.is.null"));
            rdmMemberRepository.insertWithOwner(organizationId, projectId, repositoryId, r.getId(), glProjectId, glUserId);
        });
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
    public void deletePrivilege(String data) {
        DevOpsAppServicePayload devOpsAppServicePayload = gson.fromJson(data, DevOpsAppServicePayload.class);

        Long organizationId = devOpsAppServicePayload.getOrganizationId();
        Long projectId = devOpsAppServicePayload.getIamProjectId();
        Long repositoryId = devOpsAppServicePayload.getAppServiceId();

        rdmMemberRepository.deleteByRepositoryId(organizationId, projectId, repositoryId);

        logger.info("删除应用服务后情况代码库权限成功，repositoryId：{}", repositoryId);
    }
}
