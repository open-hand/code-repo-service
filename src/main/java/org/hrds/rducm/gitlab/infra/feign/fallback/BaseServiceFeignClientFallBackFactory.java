package org.hrds.rducm.gitlab.infra.feign.fallback;

import feign.hystrix.FallbackFactory;
import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nOrgAdministratorVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
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
    @Override
    public BaseServiceFeignClient create(Throwable cause) {
        cause.printStackTrace();
        return new BaseServiceFeignClient() {
            @Override
            public ResponseEntity<List<C7nProjectVO>> listProjectsByUserIdOnOrgLevel(Long organizationId, Long userId, String name, String code, String category, Boolean enabled, Long createdBy, String params) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersWithRolesOnProjectLevel(Long projectId, String loginName, String realName, String roleName, String params) {
                return null;
            }

//            @Override
//            public ResponseEntity<List<C7nUserVO>> listProjectUsersByName(Long projectId, String param) {
//                return null;
//            }

            @Override
            public ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnProjectLevel(Long projectId, int page, int size, String loginName, String realName) {
                return null;
            }

            @Override
            public ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnOrganizationLevel(Long organizationId, int page, int size, String loginName, String realName) {
                return null;
            }

            @Override
            public ResponseEntity<Page<C7nUserVO>> pageUsersByOptionsOnSiteLevel(int page, int size, String loginName, String realName) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listProjectUsersByIds(Long projectId, Set<Long> userIds) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersWithRolesAndGitlabUserIdByIds(Long organizationId, Set<Long> userIds) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersByIds(Boolean onlyEnabled, Set<Long> ids) {
                return null;
            }

            @Override
            public ResponseEntity<Page<C7nOrgAdministratorVO>> pagingQueryOrgAdministrator(Long organizationId, int page, int size, String realName, String loginName, String params) {
                return null;
            }

            @Override
            public ResponseEntity<Boolean> checkIsOrgRoot(Long organizationId, Long userId) {
                return null;
            }

            @Override
            public ResponseEntity<Page<C7nTenantVO>> getAllOrgs() {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nProjectVO>> listProjectsByOrgId(Long organizationId) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nProjectVO>> listProjectsByIds(Set<Long> ids) {
                return null;
            }
        };
    }
}
