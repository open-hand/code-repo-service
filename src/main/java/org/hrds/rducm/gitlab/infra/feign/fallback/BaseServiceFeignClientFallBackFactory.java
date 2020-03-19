package org.hrds.rducm.gitlab.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
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
            public ResponseEntity<List<C7nUserVO>> listProjectUsersByName(Long projectId, String param) {
                return null;
            }

            @Override
            public ResponseEntity<PageInfo<C7nUserVO>> pageUsersByOptionsOnProjectLevel(Long projectId, int page, int size, String loginName, String realName) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listProjectUsersByIds(Long projectId, Set<Long> userIds) {
                return null;
            }

            @Override
            public ResponseEntity<List<C7nUserVO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
                return null;
            }
        };
    }
}
