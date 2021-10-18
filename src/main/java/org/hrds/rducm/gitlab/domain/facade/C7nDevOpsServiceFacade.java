package org.hrds.rducm.gitlab.domain.facade;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */
public interface C7nDevOpsServiceFacade {
    /**
     * 根据应用服务id获取Gitlab代码库id
     * todo 高频调用, 是否使用缓存
     *
     * @param repositoryId
     * @return
     */
    Integer repositoryIdToGlProjectId(Long repositoryId);

    /**
     * 查询应用服务信息
     *
     * @param repositoryId
     * @return
     */
    C7nAppServiceVO detailC7nAppService(Long repositoryId);

    /**
     * 查询应用服务信息
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    C7nAppServiceVO detailC7nAppServiceById(Long projectId, Long repositoryId);

    /**
     * 查询一组应用服务信息, 并转换成Map
     *
     * @param repositoryIds
     * @return
     */
    Map<Long, C7nAppServiceVO> listC7nAppServiceToMap(Set<Long> repositoryIds);

    /**
     * 项目层
     * 查询应用服务ids, 根据应用服务名模糊查询
     *
     * @param projectId
     * @param appServiceName
     * @return
     */
    Set<Long> listC7nAppServiceIdsByNameOnProjectLevel(Long projectId, String appServiceName);

    /**
     * 组织层
     * 查询应用服务ids, 根据应用服务名模糊查询
     *
     * @param organizationId
     * @param appServiceName
     * @return
     */
    Set<Long> listC7nAppServiceIdsByNameOnOrgLevel(Long organizationId, String appServiceName);

    /**
     * 项目层
     * 分页查询应用服务信息
     *
     * @param projectId
     * @param pageRequest
     * @param repositoryIds
     * @return
     */
    Page<C7nAppServiceVO> pageC7nAppServices(Long projectId, PageRequest pageRequest, Set<Long> repositoryIds);

    /**
     * 项目层
     * 获取项目的所有应用服务信息
     *
     * @param projectId
     * @return
     */
    List<C7nAppServiceVO> listC7nAppServiceOnProjectLevel(Long projectId);

    /**
     * 项目层
     * 获取项目的应用服务id对应Gitlab项目id的map
     *
     * @param projectId
     * @return
     */
    Map<Long, Long> listC7nAppServiceIdsMapOnProjectLevel(Long projectId);

    /**
     * 项目层
     * 获取项目启用的的应用服务id对应Gitlab项目id的map
     *
     * @param projectId
     * @return
     */
    Map<Long, Long> listActiveC7nAppServiceIdsMapOnProjectLevel(Long projectId);

    /**
     * 根据Gitlab用户id获取用户id
     * [glUserId1, glUserId2] => Map<glUserId, userId>
     *
     * @param glUserIds
     * @return
     */
    Map<Integer, Long> mapGlUserIdsToUserIds(Set<Integer> glUserIds);

    /**
     * 根据Gitlab用户id获取用户id
     *
     * @param glUserId
     * @return
     */
    Long glUserIdToUserId(Integer glUserId);

    /**
     * 获取项目已经启用的应用服务
     *
     * @param projectId
     * @param condition 支持编码和名称查询
     * @return
     */
    List<C7nAppServiceVO> listAppServiceByActive(Long projectId, String condition);

    /**
     * 获取devops服务项目的信息
     *
     * @param projectId
     * @return
     */
    C7nDevopsProjectVO detailDevopsProjectById(Long projectId);

    /**
     * 获取项目下已经启用的应用服务
     *
     * @param projectId
     * @return
     */
    List<C7nAppServiceVO> listActiveAppServiceByProjectId(Long projectId);

    /**
     * 根据应用服务ID数组获取服务列表
     *
     * @param repositoryIds
     * @return
     */
    List<C7nAppServiceVO> listAppServiceByIds(Set<Long> repositoryIds);

    Long getAppGroupIdByProjectId(Long projectId);


    List<C7nAppServiceVO> queryAppByProjectIds(Long projectId, List<Long> projectIds);
}
