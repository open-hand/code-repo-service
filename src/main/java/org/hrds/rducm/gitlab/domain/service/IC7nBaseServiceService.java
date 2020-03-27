package org.hrds.rducm.gitlab.domain.service;

import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */
public interface IC7nBaseServiceService {
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
     * 查询用户ids, 根据实际名称和登录名模糊查询
     *
     * @param realName
     * @param loginName
     * @return
     */
    Set<Long> listC7nUserIdsByNameOnSiteLevel(String realName, String loginName);

    /**
     * 查询项目开发成员, 并排除自己(项目层)
     *
     * @param projectId
     * @param name
     * @return
     */
    List<C7nUserVO> listDeveloperProjectMembers(Long projectId, String name);

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
}
