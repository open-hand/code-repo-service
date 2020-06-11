package org.hrds.rducm.gitlab.domain.facade.impl;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nOrgAdministratorVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.FeignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class C7NBaseServiceFacadeImpl implements C7nBaseServiceFacade {
    private static final Logger logger = LoggerFactory.getLogger(C7NBaseServiceFacadeImpl.class);

    @Autowired
    private BaseServiceFeignClient baseServiceFeignClient;

    /* 猪齿鱼用户相关方法 */

    @Override
    public Integer userIdToGlUserId(Long userId) {
        // 查询用户信息
        ResponseEntity<List<C7nUserVO>> entity = baseServiceFeignClient.listUsersByIds(false, Collections.singleton(userId));

        if (!CollectionUtils.isEmpty(entity.getBody())) {
            Long gitlabUserId = entity.getBody().get(0).getGitlabUserId();
            logger.info("{}用户获取到的GitlabUserId为{}", userId, gitlabUserId);
            return gitlabUserId == null ? null : Math.toIntExact(gitlabUserId);
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
        ResponseEntity<Page<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnProjectLevel(projectId, 0, 0, loginName, realName);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getContent();
            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Long> listC7nUserIdsByNameOnSiteLevel(String realName, String loginName) {
        // 0为不分页
        ResponseEntity<Page<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnSiteLevel(0, 0, loginName, realName);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getContent();
            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public List<C7nUserVO> listC7nUsersOnProjectLevel(Long projectId) {
        // 0为不分页
        ResponseEntity<Page<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnProjectLevel(projectId, 0, 0, null, null);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getContent();
            return c7nUserVOS;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<Long> listProjectsC7nUserIdsByNameOnOrgLevel(Long organizationId, String realName, String loginName) {
        // 查询该组织所有项目
        Set<Long> projectIds = this.listProjectIds(organizationId);

        // 使用并行流优化
        Set<Long> userIds = new HashSet<>();
        projectIds.parallelStream().forEach(projectId -> {
            Set<Long> asIds = listC7nUserIdsByNameOnProjectLevel(projectId, realName, loginName);
            userIds.addAll(asIds);
        });

        return userIds;
    }

    @Override
    public Set<Long> listC7nUserIdsByNameOnOrgLevel(Long organizationId, String realName, String loginName) {
        // 0为不分页
        ResponseEntity<Page<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnOrganizationLevel(organizationId, 0, 0, loginName, realName);

        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getContent())) {
            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getContent();
            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

//    @Override
//    public Set<Long> listC7nUserIdsByNameOnSiteLevel(String realName, String loginName) {
//        // 0为不分页
//        ResponseEntity<Page<C7nUserVO>> responseEntity = baseServiceFeignClient.pageUsersByOptionsOnSiteLevel(0, 0, loginName, realName);
//
//        if (!CollectionUtils.isEmpty(Objects.requireNonNull(responseEntity.getBody()).getList())) {
//            List<C7nUserVO> c7nUserVOS = responseEntity.getBody().getList();
//            return c7nUserVOS.stream().map(C7nUserVO::getId).collect(Collectors.toSet());
//        } else {
//            return Collections.emptySet();
//        }
//    }

    @Override
    public List<C7nUserVO> listDeveloperProjectMembers(Long projectId, String name) {
        ResponseEntity<List<C7nUserVO>> responseEntity = baseServiceFeignClient.listUsersWithRolesOnProjectLevel(projectId, null, name, null, null);

        List<C7nUserVO> c7nUserVOS = FeignUtils.handleResponseEntity(responseEntity);
        return c7nUserVOS;
    }

    @Override
    public List<C7nUserVO> listOrgAdministrator(Long organizationId) {
        ResponseEntity<Page<C7nOrgAdministratorVO>> responseEntity = baseServiceFeignClient.pagingQueryOrgAdministrator(organizationId, 0, 0, null, null, null);
        Page<C7nOrgAdministratorVO> result = FeignUtils.handleResponseEntity(responseEntity);
        return result.getContent().stream().map(r -> {
            C7nUserVO vo = new C7nUserVO();
            vo.setId(r.getId());
            vo.setRealName(r.getUserName());
            vo.setLoginName(r.getLoginName());
            return vo;
        }).collect(Collectors.toList());
    }


    /* 猪齿鱼项目相关方法 */

    @Override
    public List<C7nProjectVO> listProjectsByUserIdOnOrgLevel(Long organizationId, Long userId, String name) {
        ResponseEntity<List<C7nProjectVO>> responseEntity = baseServiceFeignClient.listProjectsByUserIdOnOrgLevel(organizationId, userId, name, null, null, null, null, null);

        List<C7nProjectVO> c7nProjectVOS = FeignUtils.handleResponseEntity(responseEntity);

        return c7nProjectVOS;
    }

    @Override
    public Set<Long> listProjectIds(Long organizationId) {
        // 查询该组织所有项目
        Set<Long> projectIds;
        ResponseEntity<List<C7nProjectVO>> responseEntity = baseServiceFeignClient.listProjectsByOrgId(organizationId);
        List<C7nProjectVO> c7nProjectVOS = FeignUtils.handleResponseEntity(responseEntity);
        projectIds = c7nProjectVOS.stream().map(C7nProjectVO::getId).collect(Collectors.toSet());

        return projectIds;
    }

    @Override
    public List<C7nProjectVO> listProjectsByIds(Set<Long> projectIds) {
        ResponseEntity<List<C7nProjectVO>> responseEntity = baseServiceFeignClient.listProjectsByIds(projectIds);
        return FeignUtils.handleResponseEntity(responseEntity);
    }

    @Override
    public Map<Long, C7nProjectVO> listProjectsByIdsToMap(Set<Long> projectIds) {
        ResponseEntity<List<C7nProjectVO>> responseEntity = baseServiceFeignClient.listProjectsByIds(projectIds);
        List<C7nProjectVO> c7nProjectVOS = FeignUtils.handleResponseEntity(responseEntity);

        if (!CollectionUtils.isEmpty(c7nProjectVOS)) {
            return c7nProjectVOS.stream().collect(Collectors.toMap(C7nProjectVO::getId, v -> v));
        } else {
            return Collections.emptyMap();
        }
    }
}
