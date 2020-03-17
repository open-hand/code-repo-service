package org.hrds.rducm.gitlab.infra.feign;

import org.hrds.rducm.gitlab.infra.feign.vo.AppServiceRepVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
@FeignClient(value = "devops-service")
public interface DevOpsFeignClient {
    /**
     * 获取所有已经启用的服务
     */
    @GetMapping("/v1/projects/{projectId}/app_service/list_by_active")
    ResponseEntity<List<AppServiceRepVO>> listRepositoriesByActive(@PathVariable Long projectId);
}
