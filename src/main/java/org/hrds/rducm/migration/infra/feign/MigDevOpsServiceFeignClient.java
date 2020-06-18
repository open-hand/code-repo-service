package org.hrds.rducm.migration.infra.feign;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.infra.feign.fallback.DevOpsServiceFeignClientFallBackFactory;
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
}
