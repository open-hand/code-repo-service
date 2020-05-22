package org.hrds.rducm.gitlab.infra.feign;

import io.choerodon.core.domain.Page;
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
     * 获取开发者角色的用户
     * 项目层查询用户列表（包括用户信息以及所分配的项目角色信息）排除自己
     *
     * @param projectId
     * @param loginName
     * @param realName
     * @param roleName
     * @param params
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}/users/search/list")
    ResponseEntity<List<C7nUserVO>> listUsersWithRolesOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                                     @RequestParam(required = false) String loginName,
                                                                     @RequestParam(required = false) String realName,
                                                                     @RequestParam(required = false) String roleName,
                                                                     @RequestParam(required = false) String params);

    /**
     * 查询项目下的用户列表(根据登录名或真实名称搜索)
     */
//    @GetMapping(value = "/v1/projects/{project_id}/users/search_by_name")
//    ResponseEntity<List<C7nUserVO>> listProjectUsersByName(@PathVariable(name = "project_id") Long projectId,
//                                                           @RequestParam(required = false) String param);


    /**
     * 项目层
     * 分页查询用户列表（包括用户信息以及所分配的项目角色信息）
     *
     * @param projectId
     * @param page      0 为不分页
     * @param size
     * @param loginName
     * @param realName
     * @return
     */
    @GetMapping("/v1/projects/{project_id}/users/search")
    ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                                     @RequestParam(required = false) int page,
                                                                     @RequestParam(required = false) int size,
                                                                     @RequestParam(required = false) String loginName,
                                                                     @RequestParam(required = false) String realName);

    /**
     * 组织层
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）
     *
     * @param organizationId
     * @param page           0为不分页
     * @param size
     * @param loginName
     * @param realName
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/users/search")
    ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnOrganizationLevel(@PathVariable(name = "organization_id") Long organizationId,
                                                                              @RequestParam(required = false) int page,
                                                                              @RequestParam(required = false) int size,
                                                                              @RequestParam(required = false) String loginName,
                                                                              @RequestParam(required = false) String realName);

    /**
     * 全局层
     * 全局层分页查询用户列表（包括用户信息以及所分配的全局角色信息）
     *
     * @param page      0为不分页
     * @param size
     * @param loginName
     * @param realName
     * @return
     */
    @GetMapping(value = "/v1/users/search")
    ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnSiteLevel(@RequestParam(required = false) int page,
                                                                      @RequestParam(required = false) int size,
                                                                      @RequestParam(required = false) String loginName,
                                                                      @RequestParam(required = false) String realName);

    /**
     * 项目层
     * 根据多个id查询用户（包括用户信息以及所分配的项目角色信息以及GitlabUserId）
     *
     * @param projectId
     * @param userIds   多个用户id
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/users/list_by_ids")
    ResponseEntity<List<C7nUserVO>> listProjectUsersByIds(@PathVariable(name = "project_id") Long projectId,
                                                          @RequestBody Set<Long> userIds);


    /**
     * 组织层
     * 根据多个id查询用户（包括用户信息以及所分配的组织角色信息以及GitlabUserId）
     *
     * @param organizationId 组织id
     * @param userIds        多个用户id
     * @return
     */
    @PostMapping(value = "v1/organizations/{organization_id}/users/list_by_ids")
    ResponseEntity<List<C7nUserVO>> listUsersWithRolesAndGitlabUserIdByIds(@PathVariable(name = "organization_id") Long organizationId,
                                                                           @RequestBody Set<Long> userIds);


    /**
     * 根据id批量查询带有gitlab用户id的用户信息列表
     *
     * @param onlyEnabled 是否只查询启用的用户
     * @param ids         用户id集合
     * @return
     */
    @PostMapping(value = "/v1/users/list_by_ids")
    ResponseEntity<List<C7nUserVO>> listUsersByIds(@RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled,
                                                   @RequestBody Set<Long> ids);

    /**
     * 查询组织下所有项目
     *
     * @param organizationId
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/projects/all")
    ResponseEntity<List<C7nProjectVO>> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId);


    /**
     * 根据id集合查询项目
     *
     * @param ids id集合，去重
     * @return 项目集合
     */
    @PostMapping("/v1/projects/ids")
    ResponseEntity<List<C7nProjectVO>> listProjectsByIds(@RequestBody Set<Long> ids);

//    /**
//     * 查询组织下所有项目
//     *
//     * @param organizationId
//     * @return
//     */
//    @GetMapping(value = "/all")
//    public ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId) {
//        return new ResponseEntity<>(organizationProjectService.listProjectsByOrgId(organizationId), HttpStatus.OK);
//    }

//    /**
//     * 根据id批量查询用户信息列表
//     *
//     * @param ids
//     * @param onlyEnabled
//     * @return
//     */
//    @PostMapping(value = "/v1/users/ids")
//    ResponseEntity<List<C7nUserVO>> listUsersByIds(@RequestBody Long[] ids,
//                                                   @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);
}
