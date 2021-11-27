package org.hrds.rducm.gitlab.domain.facade;

import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */
public interface C7nBaseServiceFacade {
    /**
     * 根据用户id获取Gitlab用户id
     * todo 高频调用, 是否使用缓存
     *
     * @param userId
     * @return
     */
    Integer userIdToGlUserId(Long userId);

    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    C7nUserVO detailC7nUser(Long userId);

    /**
     * 查询一组用户信息, 并转换成Map
     *
     * @param userIds
     * @return
     */
    Map<Long, C7nUserVO> listC7nUserToMap(Set<Long> userIds);

    /**
     * 项目层
     * 根据用户id查询项目成员用户信息
     * 附带角色信息
     *
     * @param projectId
     * @param userId
     * @return
     */
    C7nUserVO detailC7nUserOnProjectLevel(Long projectId, Long userId);

    /**
     * 查询一组用户信息, 并转换成Map
     * 附带角色信息
     *
     * @param projectId
     * @param userIds
     * @return
     */
    Map<Long, C7nUserVO> listC7nUserToMapOnProjectLevel(Long projectId, Set<Long> userIds);

    /**
     * 项目层
     * 查询用户信息, 根据实际名称和登录名模糊查询
     *
     * @param projectId
     * @param realName
     * @param loginName
     * @return
     */
    Set<Long> listC7nUserIdsByNameOnProjectLevel(Long projectId, String realName, String loginName);

    /**
     * 项目层
     * 查询用户信息, 根据实际名称和登录名和是否启用模糊查询
     *
     * @param projectId
     * @param realName
     * @param loginName
     * @param enabled
     * @return
     */
    Set<Long> listC7nUserIdsByNameOnProjectLevelAndEnabled(Long projectId, String realName, String loginName, Boolean enabled);

    /**
     * 项目层
     * 查询用户信息, 根据实际名称或登录名精确查询
     *
     * @param projectId
     * @param userName
     * @return
     */
    List<C7nUserVO> listEnabledUsersByUserName(Long projectId, String userName);

    /**
     * 平台层
     * 查询用户信息, 根据实际名称和登录名模糊查询
     *
     * @param realName
     * @param loginName
     * @return
     */
    Set<Long> listC7nUserIdsByNameOnSiteLevel(String realName, String loginName);

    /**
     * 项目层
     * 查询项目的所有用户信息
     *
     * @param projectId
     * @return
     */
    List<C7nUserVO> listC7nUsersOnProjectLevel(Long projectId);

    /**
     * 组织层
     * 查询组织下的所有项目成员的用户id(非组织层的用户), 根据实际名称和登录名模糊查询
     *
     * @param organizationId
     * @param realName
     * @param loginName
     * @return
     */
    Set<Long> listProjectsC7nUserIdsByNameOnOrgLevel(Long organizationId, String realName, String loginName);

    /**
     * 组织层
     * 查询用户ids, 根据实际名称和登录名模糊查询
     *
     * @param organizationId
     * @param realName
     * @param loginName
     * @return
     */
    Set<Long> listC7nUserIdsByNameOnOrgLevel(Long organizationId, String realName, String loginName);

    /**
     * 平台层
     * 查询用户, 根据实际名称和登录名模糊查询
     *
     * @param realName
     * @param loginName
     * @return
     */
    List<C7nUserVO> listC7nUsersByNameOnSiteLevel(String realName, String loginName);

    /**
     * 查询项目开发成员, 并排除自己(项目层)
     *
     * @param projectId
     * @param name
     * @return
     */
    List<C7nUserVO> listDeveloperProjectMembers(Long projectId, String name);

    /**
     * 获取一个组织的所有组织管理员
     *
     * @param organizationId
     * @return
     */
    List<C7nUserVO> listOrgAdministrator(Long organizationId);

    /**
     * 判断是否是组织管理员
     *
     * @param organizationId
     * @param userId
     * @return
     */
    Boolean checkIsOrgAdmin(Long organizationId, Long userId);

    /**
     * 组织层
     * 查询组织下用户的项目列表
     *
     * @param organizationId
     * @param userId
     * @param name           项目名称
     * @return
     */
    List<C7nProjectVO> listProjectsByUserIdOnOrgLevel(Long organizationId, Long userId, String name);

    /**
     * 获取组织下所有项目id
     *
     * @param organizationId
     * @return
     */
    Set<Long> listProjectIds(Long organizationId);

    /**
     * 获取项目详情
     *
     * @param projectId
     * @return
     */
    C7nProjectVO detailC7nProject(Long projectId);

    /**
     * 根据id获取项目信息
     *
     * @param projectIds
     * @return
     */
    List<C7nProjectVO> listProjectsByIds(Set<Long> projectIds);

    /**
     * 根据id获取项目信息并转换为map
     *
     * @param projectIds
     * @return
     */
    Map<Long, C7nProjectVO> listProjectsByIdsToMap(Set<Long> projectIds);

    /**
     * 获取所有组织
     *
     * @return
     */
    List<C7nTenantVO> listAllOrgs();

    /**
     * 根据项目id获取组织id
     *
     * @param projectId
     * @return
     */
    Long getOrganizationId(Long projectId);

    List<C7nUserVO> listCustomGitlabOwnerLableUser(Long projectId, String roleLabel);

    List<C7nUserVO> listRoot();

}
