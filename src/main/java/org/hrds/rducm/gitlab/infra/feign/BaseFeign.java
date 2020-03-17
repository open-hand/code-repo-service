package org.hrds.rducm.gitlab.infra.feign;

import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.infra.feign.vo.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/16
 */
@FeignClient(value = "base-service")
public interface BaseFeign {
    /**
     * 查询用户信息
     *
     * @param organizationId organizationId
     * @param id             id
     * @return UserDTO
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/users/{id}")
    ResponseEntity<UserDTO> query(@PathVariable(name = "organization_id") Long organizationId,
                                  @PathVariable("id") Long id);

    /**
     * 获取当前用户所有的项目
     */
    void listProjects(Long organizationId);



    /**
     * 获取开发者角色的用户
     */
    void listDeveloperUsers(Long project);

    /**
     * 根据用户名或登录名模糊搜索用户信息
     */
    List getUserIdsByOptions(String username, String loginName);

    /**
     * 根据应用服务名模糊搜索应用服务信息
     */
    List getRepositoryIdsByOptions(String appServiceName);

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
