package org.hrds.rducm.gitlab.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
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
    private ObjectMapper objectMapper;

    @Override
    public Integer repositoryIdToGlProjectId(Long projectId, Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, Collections.singleton(repositoryId), false, null, null, null, "{}");

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getList())) {
            return Math.toIntExact(entity.getBody().getList().get(0).getGitlabProjectId());
        } else {
            return null;
        }
    }

    @Override
    public C7nAppServiceVO detailC7nAppService(Long projectId, Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, Collections.singleton(repositoryId), false, null, null, null, "{}");

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getList())) {
            return entity.getBody().getList().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, C7nAppServiceVO> listC7nAppServiceToMap(Long projectId, Set<Long> repositoryIds) {
        if (repositoryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询应用服务信息
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, repositoryIds, false, null, null, null, "{}");

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getList())) {
            return entity.getBody().getList().stream().collect(Collectors.toMap(C7nAppServiceVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public List<C7nAppServiceVO> listC7nAppServicesByName(Long projectId, String appServiceName) {
        // 将参数转换为json格式
        String param = null;
        if (appServiceName != null) {
            Map<String, Map<String, String>> paramMap = Maps.newHashMap();
            Map<String, String> searchParamMap = Maps.newHashMap();
            searchParamMap.put("name", appServiceName);
            paramMap.put("searchParam", searchParamMap);

            try {
                param = objectMapper.writeValueAsString(paramMap);
            } catch (JsonProcessingException e) {
                throw new CommonException(e);
            }
        }

        ResponseEntity<PageInfo<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, null, true, null, null, null, param);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getList())) {
            return responseEntity.getBody().getList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public PageInfo<C7nAppServiceVO> pageC7nAppServices(Long projectId, PageRequest pageRequest, Set<Long> repositoryIds) {
        // 这里加1是因为在controller被-1
        int page = pageRequest.getPage() + 1;
        int size = pageRequest.getSize();

        // todo 根据repositoryIds查询
        ResponseEntity<PageInfo<C7nAppServiceVO>> responseEntity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, repositoryIds, true, null, page, size, "{}");

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getList())) {
            return responseEntity.getBody();
        } else {
            return PageInfo.of(Collections.emptyList());
        }
    }
}
