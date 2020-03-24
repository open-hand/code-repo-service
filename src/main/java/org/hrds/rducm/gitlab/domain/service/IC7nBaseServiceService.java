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
     * @param projectId
     * @param userId
     * @return
     */
    Integer userIdToGlUserId(Long projectId, Long userId);

    /**
     * 查询用户信息
     *
     * @param projectId
     * @param userId
     * @return
     */
    C7nUserVO detailC7nUser(Long projectId, Long userId);

    /**
     * 查询一组用户信息, 并转换成Map
     *
     * @param projectId
     * @param userIds
     * @return
     */
    Map<Long, C7nUserVO> listC7nUserToMap(Long projectId, Set<Long> userIds);

    /**
     * 项目层
     * 查询用户信息, 根据实际名称和登录名模糊查询
     *
     * @param projectId
     * @param realName
     * @param loginName
     * @return
     */
    List<C7nUserVO> listC7nUsersByName(Long projectId, String realName, String loginName);

    /**
     * 组织层
     * 查询用户信息, 根据实际名称和登录名模糊查询
     *
     * @param organizationId
     * @param realName
     * @param loginName
     * @return
     */
    List<C7nUserVO> listC7nUsersByNameOnOrgLevel(Long organizationId, String realName, String loginName);

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
     * @param name 项目名称
     * @return
     */
    List<C7nProjectVO> listProjectsByUserIdOnOrgLevel(Long organizationId, Long userId, String name);
}
