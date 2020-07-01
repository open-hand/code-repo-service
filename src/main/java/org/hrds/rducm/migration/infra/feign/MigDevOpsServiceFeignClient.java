package org.hrds.rducm.migration.infra.feign;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.infra.feign.fallback.DevOpsServiceFeignClientFallBackFactory;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.migration.infra.feign.vo.DevopsUserPermissionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/12
 */
@FeignClient(value = "devops-service")
public interface MigDevOpsServiceFeignClient {
    /**
     * 查询拥有应用服务权限的用户
     *
     * @param projectId    项目ID
     * @param appServiceId 服务服务Id
     * @param size
     * @param page
     * @param searchParam  查询参数
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_service/{app_service_id}/page_permission_users")
    ResponseEntity<Page<DevopsUserPermissionVO>> pagePermissionUsers(
            @PathVariable(value = "project_id") Long projectId,
            @PathVariable(value = "app_service_id", required = false) Long appServiceId,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size,
            @RequestBody(required = false) String searchParam);

    //    // FIXME 废弃
    /**
     * 项目下分页查询应用服务
     *
     * @param projectId
     * @param isActive    服务是否启用
     * @param checkMember 是否校验团队成员权限
     * @param doPage
     * @param page
     * @param size
     * @param params
     * @return
     */
    @PostMapping("/v1/projects/{project_id}/app_service/page_by_options")
    ResponseEntity<Page<C7nAppServiceVO>> pageAppServiceByOptions(@PathVariable(value = "project_id") Long projectId,
                                                                  @RequestParam(value = "active", required = false) Boolean isActive,
                                                                  @RequestParam(value = "checkMember", required = false) Boolean checkMember,
                                                                  @RequestParam(value = "doPage", required = false) Boolean doPage,
                                                                  @RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestBody(required = false) String params);

}
