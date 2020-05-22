package org.hrds.rducm.gitlab.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nGlUserVO;
import org.hrds.rducm.gitlab.infra.util.FeignUtils;
import org.hrds.rducm.gitlab.infra.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */

@Service
public class C7nDevOpsServiceServiceImpl implements IC7nDevOpsServiceService {
    @Autowired
    private DevOpsServiceFeignClient devOpsServiceFeignClient;
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Integer repositoryIdToGlProjectId(Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<Page<C7nAppServiceVO>> entity = devOpsServiceFeignClient.listAppServiceByIds(Collections.singleton(repositoryId));

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getContent())) {
            return Math.toIntExact(entity.getBody().getContent().get(0).getGitlabProjectId());
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

        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageAppServiceByOptions(projectId, false, 0, 0, params);

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
        Set<Long> projectIds = ic7nBaseServiceService.listProjectIds(organizationId);

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
        // 这里加1是因为在controller被-1
        int page = pageRequest.getPage() + 1;
        int size = pageRequest.getSize();

        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.listOrPageProjectAppServices(projectId, Optional.ofNullable(repositoryIds).orElse(Collections.emptySet()), true, page, size);
        return FeignUtils.handleResponseEntity(responseEntity);
    }

    @Override
    public List<C7nAppServiceVO> listC7nAppServiceOnProjectLevel(Long projectId) {
        // 将参数转换为json格式
        ResponseEntity<Page<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageAppServiceByOptions(projectId, false, 0, 0, "");

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
                    .collect(Collectors.toMap(C7nAppServiceVO::getId, C7nAppServiceVO::getGitlabProjectId));
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
}
