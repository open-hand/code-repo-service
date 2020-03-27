package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.FeignUtils;
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
public class C7nBaseServiceServiceImpl implements IC7nBaseServiceService {
    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    /* 猪齿鱼用户相关方法 */

    @Override
    public Integer userIdToGlUserId(Long userId) {
        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listUsersByIds(false, Collections.singleton(userId));

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return Math.toIntExact(entity.getBody().get(0).getGitlabUserId());
        } else {
            return null;
        }
    }

    @Override
    public C7nUserVO detailC7nUser(Long userId) {
        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listUsersByIds(false, Collections.singleton(userId));

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return entity.getBody().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<Long, C7nUserVO> listC7nUserToMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listUsersByIds(false, userIds);

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return entity.getBody().stream().collect(Collectors.toMap(C7nUserVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<Long, C7nUserVO> listC7nUserToMapOnProjectLevel(Long projectId, Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询用户信息, 附带角色信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listProjectUsersByIds(projectId, userIds);

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            return entity.getBody().stream().collect(Collectors.toMap(C7nUserVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Set<Long> listC7nUserIdsByNameOnProjectLevel(Long projectId, String realName, String loginName) {
        // 0为不分页
        ResponseEntity<PageInfo<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnProjectLevel(projectId, 0, 0, loginName, realName);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getList())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getList();
            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Long> listC7nUserIdsByNameOnOrgLevel(Long organizationId, String realName, String loginName) {
        // 0为不分页
        ResponseEntity<PageInfo<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnOrganizationLevel(organizationId, 0, 0, loginName, realName);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getList())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getList();
            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Long> listC7nUserIdsByNameOnSiteLevel(String realName, String loginName) {
        // 0为不分页
        ResponseEntity<PageInfo<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnSiteLevel(0, 0, loginName, realName);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getList())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getList();
            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public List<C7nUserVO> listDeveloperProjectMembers(Long projectId, String name) {
        ResponseEntity<List<C7nUserVO>> responseEntity = baseServiceFeignClient.listUsersWithRolesOnProjectLevel(projectId, null, name, null, null);

        List<C7nUserVO> c7nUserVOS = FeignUtils.handleResponseEntity(responseEntity);
        return c7nUserVOS;
    }

    /* 猪齿鱼项目相关方法 */

    @Override
    public List<C7nProjectVO> listProjectsByUserIdOnOrgLevel(Long organizationId, Long userId, String name) {
        ResponseEntity<List<C7nProjectVO>> responseEntity = baseServiceFeignClient.listProjectsByUserIdOnOrgLevel(organizationId, userId, name, null, null, null, null, null);

        List<C7nProjectVO> c7nProjectVOS = FeignUtils.handleResponseEntity(responseEntity);

        return c7nProjectVOS;
    }
}
