package org.hrds.rducm.gitlab.infra.feign.fallback;

import feign.hystrix.FallbackFactory;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;

import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nOrgAdministratorVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/16
 */
@Component
public class BaseServiceFeignClientFallBackFactory implements FallbackFactory<BaseServiceFeignClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceFeignClientFallBackFactory.class);

    @Override
    public BaseServiceFeignClient create(Throwable cause) {
        LOGGER.error("error.feign.base", cause);
        return new BaseServiceFeignClient() {
            @Override
            public ResponseEntity<List<C7nProjectVO>> listProjectsByUserIdOnOrgLevel(Long organizationId, Long userId, String name, String code, String category, Boolean enabled, Long createdBy, String params) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersWithRolesOnProjectLevel(Long projectId, String loginName, String realName, String roleName, String params) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listEnabledUsersByUserName(Long projectId, String userName) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnProjectLevel(Long projectId, int page, int size, String loginName, String realName, Boolean enabled) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnOrganizationLevel(Long organizationId, int page, int size, String loginName, String realName) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnSiteLevel(int page, int size, String loginName, String realName, String params) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listProjectUsersByIds(Long projectId, Set<Long> userIds) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersWithRolesAndGitlabUserIdByIds(Long organizationId, Set<Long> userIds) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersByIds(Boolean onlyEnabled, Set<Long> ids) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nOrgAdministratorVO>> pagingQueryOrgAdministrator(Long organizationId, int page, int size, String realName, String loginName, String params) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Boolean> checkIsOrgRoot(Long organizationId, Long userId) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nTenantVO>> getAllOrgs(int page, int size) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nProjectVO>> listProjectsByOrgId(Long organizationId) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nProjectVO>> listProjectsByIds(Set<Long> ids) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listProjectUsersByProjectIdAndRoleLable(Long projectId, String roleLable) {
                throw new CommonException("error.feign.fallback");
            }
        };
    }
}
