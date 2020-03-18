package org.hrds.rducm.gitlab.domain.service;

import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;

import java.util.Map;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */
public interface IC7nDevOpsServiceService {
    /**
     * 根据应用服务id获取Gitlab代码库id
     * todo 高频调用, 是否使用缓存
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    Integer repositoryIdToGlProjectId(Long projectId, Long repositoryId);

    /**
     * 查询应用服务信息
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    C7nAppServiceVO detailC7nAppService(Long projectId, Long repositoryId);

    /**
     * 查询一组应用服务信息, 并转换成Map
     *
     * @param projectId
     * @param repositoryIds
     * @return
     */
    Map<Long, C7nAppServiceVO> listC7nAppServiceToMap(Long projectId, Set<Long> repositoryIds);
}
