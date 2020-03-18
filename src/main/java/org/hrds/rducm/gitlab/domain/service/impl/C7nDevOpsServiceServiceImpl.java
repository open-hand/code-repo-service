package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
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

    @Override
    public Integer repositoryIdToGlProjectId(Long projectId, Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, Collections.singleton(repositoryId), false);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getList())) {
            return Math.toIntExact(entity.getBody().getList().get(0).getGitlabProjectId());
        } else {
            return null;
        }
    }

    @Override
    public C7nAppServiceVO detailC7nAppService(Long projectId, Long repositoryId) {
        // 查询应用服务信息
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, Collections.singleton(repositoryId), false);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getList())) {
            return entity.getBody().getList().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, C7nAppServiceVO> listC7nAppServiceToMap(Long projectId, Set<Long> repositoryIds) {
        // 查询应用服务信息
        ResponseEntity<PageInfo<C7nAppServiceVO>> entity = devOpsServiceFeignClient.pageProjectAppServiceByIds(projectId, repositoryIds, false);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(entity.getBody()).getList())) {
            return entity.getBody().getList().stream().collect(Collectors.toMap(C7nAppServiceVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }
}
