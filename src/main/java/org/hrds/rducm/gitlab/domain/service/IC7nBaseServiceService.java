package org.hrds.rducm.gitlab.domain.service;

import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;

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
}
