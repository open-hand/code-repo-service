package org.hrds.rducm.gitlab.infra.feign;

import com.github.pagehelper.PageInfo;
import org.hrds.rducm.gitlab.infra.feign.fallback.DevOpsServiceFeignClientFallBackFactory;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
@FeignClient(value = "devops-service", fallbackFactory = DevOpsServiceFeignClientFallBackFactory.class)
public interface DevOpsServiceFeignClient {
    /**
     * 获取所有已经启用的服务
     */
    @GetMapping("/v1/projects/{projectId}/app_service/list_by_active")
    ResponseEntity<List<C7nAppServiceVO>> listRepositoriesByActive(@PathVariable Long projectId);

    /**
     * @param projectId
     * @param doPage
     * @param params
     */
//    @ApiOperation(value = "项目下分页查询应用服务")
//    @PostMapping("/v1/projects/{project_id}/app_service/page_by_options")
//    ResponseEntity<PageInfo<C7nAppServiceVO>> pageAppServiceByOptions(@ApiParam(value = "项目Id", required = true) @PathVariable(value = "project_id") Long projectId,
//                                                                      @ApiParam(value = "是否分页") @RequestParam(value = "doPage", required = false) Boolean doPage,
//                                                                      @RequestParam(required = false) Integer page,
//                                                                      @RequestParam(required = false) Integer size,
//                                                                      @ApiParam(value = "查询参数") @RequestBody(required = false) String params);


    /**
     * 批量查询应用服务
     *
     * @param projectId   项目Id
     * @param ids         应用服务Ids
     * @param doPage      是否分页
     * @param withVersion 是否需要版本信息
     * @param page        页数
     * @param size        每页大小
     * @param params      查询参数
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_service/list_app_service_ids")
    ResponseEntity<PageInfo<C7nAppServiceVO>> pageProjectAppServiceByIds(@PathVariable(value = "project_id") Long projectId,
                                                                         @RequestParam(value = "ids") Set<Long> ids,
                                                                         @RequestParam(value = "doPage", required = false) Boolean doPage,
                                                                         @RequestParam(value = "with_version", required = false, defaultValue = "true") Boolean withVersion,
                                                                         @RequestParam(required = false) Integer page,
                                                                         @RequestParam(required = false) Integer size,
                                                                         @RequestBody(required = false) String params);

    /**
     * 通过一组id分页查询或者不传id时进行分页查询
     *
     * @param projectId 项目Id
     * @param ids       应用服务Ids
     * @param doPage    是否分页
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}/app_service/list_by_ids_or_page")
    ResponseEntity<PageInfo<C7nAppServiceVO>> listOrPageProjectAppServices(@PathVariable(value = "project_id") Long projectId,
                                                                           @RequestParam(value = "ids", required = false) Set<Long> ids,
                                                                           @RequestParam(value = "doPage", required = false, defaultValue = "true") Boolean doPage,
                                                                           @RequestParam(required = false) Integer page,
                                                                           @RequestParam(required = false) Integer size);
}
