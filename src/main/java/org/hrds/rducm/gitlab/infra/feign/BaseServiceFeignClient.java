package org.hrds.rducm.gitlab.infra.feign;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.infra.feign.fallback.BaseServiceFeignClientFallBackFactory;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/16
 */
@FeignClient(value = "base-service", fallbackFactory = BaseServiceFeignClientFallBackFactory.class)
public interface BaseServiceFeignClient {
    /**
     * 查询当前组织下用户的项目列表
     *
     * @param organizationId
     * @param userId
     * @param name
     * @param code
     * @param category
     * @param enabled
     * @param createdBy
     * @param params
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/users/{user_id}/projects")
    ResponseEntity<List<C7nProjectVO>> listProjectsByUserIdOnOrgLevel(@PathVariable(name = "organization_id") Long organizationId,
                                                                      @PathVariable(name = "user_id") Long userId,
                                                                      @RequestParam(required = false) String name,
                                                                      @RequestParam(required = false) String code,
                                                                      @RequestParam(required = false) String category,
                                                                      @RequestParam(required = false) Boolean enabled,
                                                                      @RequestParam(required = false) Long createdBy,
                                                                      @RequestParam(required = false) String params);

    /**
     * 1. 获取当前用户所有的项目
     * 2. 获取“开发者角色”的团队成员信息，并排除自己          X
     * 3. 根据“用户名或登录名”模糊搜索团队成员信息(不分页)     X
     * 4. 根据“应用服务名”模糊搜索应用服务信息(不分页)        √  /v1/projects/{project_id}/app_service/page_by_options
     * 5. 根据一组用户id,获取用户信息(需包含gitlab用户id)    X
     * 6. 根据一组应用服务id,获取应用服务信息(需包含gitlab代码库id) X
     */


    /**
     * 获取开发者角色的用户 todo 暂时是所有项目成员
     */
    @ApiOperation(value = "查询项目下的用户列表(根据登录名或真实名称搜索)")
    @GetMapping(value = "/v1/projects/{project_id}/users/search_by_name")
    ResponseEntity<List<C7nUserVO>> listProjectUsersByName(@PathVariable(name = "project_id") Long projectId,
                                                           @RequestParam(required = false) String param);


    /**
     * 项目层
     * 分页查询用户列表（包括用户信息以及所分配的项目角色信息）
     *
     * @param projectId
     * @param page
     * @param size
     * @param loginName
     * @param realName
     * @return
     */
    @GetMapping("/v1/projects/{project_id}/users/search")
    ResponseEntity<PageInfo<C7nUserVO>> pageUsersByOptionsOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                                         @RequestParam(required = false) int page,
                                                                         @RequestParam(required = false) int size,
                                                                         @RequestParam(required = false) String loginName,
                                                                         @RequestParam(required = false) String realName);

    /**
     * 根据多个id查询用户（包括用户信息以及所分配的项目角色信息以及GitlabUserId）
     *
     * @param projectId
     * @param userIds   多个用户id
     * @return
     */
    @ApiOperation(value = "根据多个id查询用户（包括用户信息以及所分配的项目角色信息以及GitlabUserId）")
    @GetMapping(value = "/v1/projects/{project_id}/users/list_by_ids")
    ResponseEntity<List<C7nUserVO>> listProjectUsersByIds(@PathVariable(name = "project_id") Long projectId,
                                                          @RequestParam(name = "user_ids") Set<Long> userIds);


    /**
     * 根据id批量查询用户信息列表
     *
     * @param ids
     * @param onlyEnabled
     * @return
     */
    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<C7nUserVO>> listUsersByIds(@RequestBody Long[] ids,
                                                   @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);
}
