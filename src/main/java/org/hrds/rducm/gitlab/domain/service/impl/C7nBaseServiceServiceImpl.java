package org.hrds.rducm.gitlab.domain.service.impl;

import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/18
 */

@Service
public class C7nBaseServiceServiceImpl implements IC7nBaseServiceService {
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    @Override
    public Integer userIdToGlUserId(Long projectId, Long userId) {
        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listProjectUsersByIds(projectId, Collections.singleton(userId));

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return Math.toIntExact(entity.getBody().get(0).getGitlabUserId());
        } else {
            return null;
        }
    }

    @Override
    public C7nUserVO detailC7nUser(Long projectId, Long userId) {
        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listProjectUsersByIds(projectId, Collections.singleton(userId));

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return entity.getBody().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, C7nUserVO> listC7nUserToMap(Long projectId, Set<Long> userIds) {
        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listProjectUsersByIds(projectId, userIds);

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return entity.getBody().stream().collect(Collectors.toMap(C7nUserVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }
}
