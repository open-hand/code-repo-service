package org.hrds.rducm.gitlab.infra.feign;

import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.infra.feign.vo.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/16
 */
@FeignClient(value = "base-service")
public interface BaseServiceFeignClient {
//    /**
//     * 查询用户信息
//     *
//     * @param organizationId organizationId
//     * @param id             id
//     * @return UserDTO
//     */
//    @GetMapping(value = "/v1/organizations/{organization_id}/users/{id}")
//    ResponseEntity<UserDTO> query(@PathVariable(name = "organization_id") Long organizationId,
//                                  @PathVariable("id") Long id);

    /**
     * 1. 获取当前用户所有的项目
     * 2. 获取“开发者角色”的团队成员信息，并排除自己
     * 3. 根据“用户名或登录名”模糊搜索团队成员信息     X
     * 4. 根据“应用服务名”模糊搜索应用服务信息        √
     * 5. 根据一组用户id,获取Gitlab用户id
     * 6. 根据一组应用服务id,获取Gitlab代码库id
     */

//    /**
//     * 获取当前用户所有的项目
//     */
//    void listProjects(Long organizationId);
//
//
//

    /**
     * 获取开发者角色的用户
     */
//    void listDeveloperUsers(Long project);

    /**
     * 获取开发者角色的用户 todo 暂时是所有项目成员
     */
    @ApiOperation(value = "查询项目下的用户列表(根据登录名或真实名称搜索)")
    @GetMapping(value = "/v1/projects/{project_id}/users/search_by_name")
    ResponseEntity<List<UserDTO>> listProjectUsersByName(@PathVariable(name = "project_id") Long projectId,
                                                         @RequestParam(required = false) String param);

//
//    /**
//     * 根据projectId和param模糊查询loginName和realName两列
//     * 根据用户名或登录名模糊搜索用户信息
//     */
//    @GetMapping("/{project_id}/users/search_by_name")
//    List getUserIdsByOptions(@PathVariable(name = "project_id") Long projectId,
//                             @RequestParam(required = false) String param);
//
//    /**
//     * 根据应用服务名模糊搜索应用服务信息
//     */
//    List getRepositoryIdsByOptions(String appServiceName);

//    /**
//     * 根据一组用户id,获取用户信息
//     *
//     */
//    List listUsersByUserIds(List<Long> userIds);
//
//    /**
//     * 根据一组代码库id,获取代码库信息
//     *
//     */
//    List listRepositoriesByRepositoryIds(List<Long> repositoryIds);
}
