package org.hrds.rducm.gitlab.infra.feign;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rducm.gitlab.infra.feign.vo.AppServiceRepVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
@FeignClient(value = "devops-service")
public interface DevOpsServiceFeignClient {
    /**
     * 获取所有已经启用的服务
     */
    @GetMapping("/v1/projects/{projectId}/app_service/list_by_active")
    ResponseEntity<List<AppServiceRepVO>> listRepositoriesByActive(@PathVariable Long projectId);

    /**
     * @param projectId
     * @param doPage
     * @param params
     */
    @ApiOperation(value = "项目下分页查询应用服务")
    @PostMapping("/v1/projects/{project_id}/app_service/page_by_options")
    ResponseEntity<PageInfo<AppServiceRepVO>> pageAppServiceByOptions(@ApiParam(value = "项目Id", required = true)
                                                                      @PathVariable(value = "project_id") Long projectId,
                                                                      @ApiParam(value = "是否分页")
                                                                      @RequestParam(value = "doPage", required = false) Boolean doPage,
                                                                      @ApiParam(value = "查询参数")
                                                                      @RequestBody(required = false) String params);
}
