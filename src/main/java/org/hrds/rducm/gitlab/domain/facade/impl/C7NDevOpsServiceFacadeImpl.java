package org.hrds.rducm.gitlab.domain.facade.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nGlUserVO;
import org.hrds.rducm.gitlab.infra.util.FeignUtils;
import org.hrds.rducm.gitlab.infra.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */

@Service
public class C7NDevOpsServiceFacadeImpl implements C7nDevOpsServiceFacade {
    private static final Logger logger = LoggerFactory.getLogger(C7NDevOpsServiceFacadeImpl.class);
    @Autowired
    private DevOpsServiceFeignClient devOpsServiceFeignClient;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Integer repositoryIdToGlProjectId(Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<Page<C7nAppServiceVO>> entity = devOpsServiceFeignClient.listAppServiceByIds(Collections.singleton(repositoryId));

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getContent())) {
            Long gitlabProjectId = entity.getBody().get(0).getGitlabProjectId();
            logger.info("{}应用服务获取到的GitlabProjectId为{}", repositoryId, gitlabProjectId);
            return gitlabProjectId == null ? null : Math.toIntExact(gitlabProjectId);
        } else {
            return null;
        }
    }

    @Override
    public C7nAppServiceVO detailC7nAppService(Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<Page<C7nAppServiceVO>> entity = devOpsServiceFeignClient.listAppServiceByIds(Collections.singleton(repositoryId));

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getContent())) {
            return entity.getBody().getContent().get(0);
        } else {
            return null;
        }
    }

    @Override
    public C7nAppServiceVO detailC7nAppServiceById(Long projectId, Long repositoryId) {
        ResponseEntity<C7nAppServiceVO> entity = devOpsServiceFeignClient.getAppServiceById(projectId, repositoryId);
        if (Objects.nonNull(entity.getBody())) {
            return entity.getBody();
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, C7nAppServiceVO> listC7nAppServiceToMap(Set<Long> repositoryIds) {
        if (repositoryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询应用服务信息
        ResponseEntity<Page<C7nAppServiceVO>> entity = devOpsServiceFeignClient.listAppServiceByIds(repositoryIds);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getContent())) {
            return entity.getBody().getContent().stream().collect(Collectors.toMap(C7nAppServiceVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Set<Long> listC7nAppServiceIdsByNameOnProjectLevel(Long projectId, String appServiceName) {
        // 将参数转换为json格式
        String params = TypeUtil.castToSearchParam("name", appServiceName);

        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageAppService(projectId, 0, 0, params);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nAppServiceVO> c7nAppServiceVOS = responseEntity.getBody().getContent();
            return c7nAppServiceVOS.stream().map(C7nAppServiceVO::getId).collect(Collectors.toSet());

        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Long> listC7nAppServiceIdsByNameOnOrgLevel(Long organizationId, String appServiceName) {
        // 查询该组织所有项目
        Set<Long> projectIds = c7NBaseServiceFacade.listProjectIds(organizationId);

        // 查询项目的应用服务
        Set<Long> appServiceIds = new HashSet<>();
        // 使用并行流优化
        projectIds.parallelStream().forEach(projectId -> {
            Set<Long> asIds = listC7nAppServiceIdsByNameOnProjectLevel(projectId, appServiceName);
            appServiceIds.addAll(asIds);
        });

        return appServiceIds;
    }

    @Override
    public Page<C7nAppServiceVO> pageC7nAppServices(Long projectId, PageRequest pageRequest, Set<Long> repositoryIds) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();

        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.listOrPageProjectAppServices(projectId, Optional.ofNullable(repositoryIds).orElse(Collections.emptySet()), true, page, size);
        return FeignUtils.handleResponseEntity(responseEntity);
    }

    @Override
    public List<C7nAppServiceVO> listC7nAppServiceOnProjectLevel(Long projectId) {
        // 将参数转换为json格式
        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageAppService(projectId, 0, 0, "{}");

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nAppServiceVO> c7nAppServiceVOS = responseEntity.getBody().getContent();
            return c7nAppServiceVOS;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Map<Long, Long> listC7nAppServiceIdsMapOnProjectLevel(Long projectId) {
        List<C7nAppServiceVO> list = this.listC7nAppServiceOnProjectLevel(projectId);

        if (!CollectionUtils.isEmpty(list)) {
            return list.stream()
                    .collect(HashMap::new, (m, v) ->
                            m.put(v.getId(), v.getGitlabProjectId()), HashMap::putAll);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<Long, Long> listActiveC7nAppServiceIdsMapOnProjectLevel(Long projectId) {
        List<C7nAppServiceVO> list = this.listC7nAppServiceOnProjectLevel(projectId);
        list = list.stream().filter(C7nAppServiceVO::getActive).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(list)) {
            return list.stream()
                    .collect(HashMap::new, (m, v) ->
                            m.put(v.getId(), v.getGitlabProjectId()), HashMap::putAll);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<Integer, Long> mapGlUserIdsToUserIds(Set<Integer> glUserIds) {
        ResponseEntity<List<C7nGlUserVO>> responseEntity = devOpsServiceFeignClient.listUsersByGitlabUserIds(glUserIds);

        List<C7nGlUserVO> c7nGlUserVOS = FeignUtils.handleResponseEntity(responseEntity);

        Map<Integer, Long> result = new HashMap<>();
        c7nGlUserVOS.forEach(vo -> result.put(Math.toIntExact(vo.getGitlabUserId()), vo.getIamUserId()));

        return result;
    }

    @Override
    public Long glUserIdToUserId(Integer glUserId) {
        ResponseEntity<List<C7nGlUserVO>> responseEntity = devOpsServiceFeignClient.listUsersByGitlabUserIds(Collections.singleton(glUserId));

        List<C7nGlUserVO> c7nGlUserVOS = FeignUtils.handleResponseEntity(responseEntity);

        if (!CollectionUtils.isEmpty(c7nGlUserVOS)) {
            return c7nGlUserVOS.get(0).getIamUserId();
        } else {
            return null;
        }
    }

    @Override
    public List<C7nAppServiceVO> listAppServiceByActive(Long projectId, String condition) {
        // 获取所有已经启用的服务
        // 将参数转换为json格式
        String params = new TypeUtil.ParamsBuilder()
                .searchParam("active", "1")
                .param(condition)
                .build();

        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageAppService(projectId, 0, 0, params);

        return FeignUtils.handleResponseEntity(responseEntity).getContent();
    }

    @Override
    public C7nDevopsProjectVO detailDevopsProjectById(Long projectId) {
        ResponseEntity<List<C7nDevopsProjectVO>> responseEntity = devOpsServiceFeignClient.listDevopsProjectByIds(projectId, Sets.newHashSet(projectId));

        List<C7nDevopsProjectVO> c7nDevopsProjectVOS = FeignUtils.handleResponseEntity(responseEntity);
        if (c7nDevopsProjectVOS.isEmpty()) {
            return null;
        } else {
            return c7nDevopsProjectVOS.get(0);
        }
    }

    @Override
    public List<C7nAppServiceVO> listActiveAppServiceByProjectId(Long projectId) {
        ResponseEntity<List<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.listAppServiceByActive(projectId);
        List<C7nAppServiceVO> c7nAppServiceVOS = FeignUtils.handleResponseEntity(responseEntity);
        if (c7nAppServiceVOS.isEmpty()) {
            return null;
        } else {
            return c7nAppServiceVOS;
        }
    }

    @Override
    public List<C7nAppServiceVO> listAppServiceByIds(Set<Long> repositoryIds) {
        if (repositoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询应用服务信息
        ResponseEntity<Page<C7nAppServiceVO>> entity = devOpsServiceFeignClient.listAppServiceByIds(repositoryIds);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getContent())) {
            return entity.getBody().getContent();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Long getAppGroupIdByProjectId(Long projectId) {
        ResponseEntity<Long> appGroupIdByProjectId = devOpsServiceFeignClient.getAppGroupIdByProjectId(projectId);

        if (Objects.requireNonNull(appGroupIdByProjectId.getBody()) != null) {
            return appGroupIdByProjectId.getBody();
        } else {
            return null;
        }
    }
}
