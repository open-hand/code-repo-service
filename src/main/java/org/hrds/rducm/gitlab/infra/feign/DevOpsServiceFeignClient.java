package org.hrds.rducm.gitlab.infra.feign;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.infra.feign.fallback.DevOpsServiceFeignClientFallBackFactory;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nGlUserVO;
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
    ResponseEntity<List<C7nAppServiceVO>> listAppServiceByActive(@PathVariable Long projectId);

    /**
     * 内部查询项目下的应用服务 / 不区分权限
     *
     * @param projectId
     * @param page
     * @param size
     * @param params
     * @return
     */
    @PostMapping("/v1/projects/{project_id}/app_service/page_by_options_internal")
    ResponseEntity<Page<C7nAppServiceVO>> pageAppService(@PathVariable("project_id") Long projectId,
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
    @PostMapping(value = "/v1/projects/{project_id}/app_service/list_by_ids_or_page")
    ResponseEntity<Page<C7nAppServiceVO>> listOrPageProjectAppServices(@PathVariable(value = "project_id") Long projectId,
                                                                       @RequestBody(required = false) Set<Long> ids,
                                                                       @RequestParam(value = "doPage", required = false, defaultValue = "true") Boolean doPage,
                                                                       @RequestParam(required = false) Integer page,
                                                                       @RequestParam(required = false) Integer size);


    /**
     * 批量查询应用服务
     *
     * @param ids 应用服务Ids, 不能为空，也不能为空数组
     * @return
     */
    @PostMapping(value = "/v1/app_service/list_app_service_by_ids")
    ResponseEntity<Page<C7nAppServiceVO>> listAppServiceByIds(@RequestBody Set<Long> ids);

    /**
     * 根据一组Gitlab用户id查询用户信息
     *
     * @param gitlabUserIds
     * @return
     */
    @PostMapping(value = "v1/users/list_by_gitlab_user_ids")
    ResponseEntity<List<C7nGlUserVO>> listUsersByGitlabUserIds(@RequestBody Set<Integer> gitlabUserIds);

    /* 项目相关 */

    /**
     * 查询项目信息(带有gitlab groupId)
     *
     * @param projectId  无用参数
     * @param projectIds 项目ids
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/gitlab_groups")
    ResponseEntity<List<C7nDevopsProjectVO>> listDevopsProjectByIds(@PathVariable(value = "project_id") Long projectId,
                                                                    @RequestBody Set<Long> projectIds);

    /**
     * 查询单个服务信息
     *
     * @param projectId  项目ID
     * @param appServiceId 应用服务ID
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}/app_service/{app_service_id}")
    ResponseEntity<C7nAppServiceVO> getAppServiceById(@PathVariable(value = "project_id") Long projectId,
                                                      @PathVariable(value = "app_service_id") Long appServiceId);
}
