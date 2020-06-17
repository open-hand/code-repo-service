package org.hrds.rducm.gitlab.infra.feign.fallback;

import feign.hystrix.FallbackFactory;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nGlUserVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/16
 */
@Component
public class DevOpsServiceFeignClientFallBackFactory implements FallbackFactory<DevOpsServiceFeignClient> {
    @Override
    public DevOpsServiceFeignClient create(Throwable cause) {
        cause.printStackTrace();
        return new DevOpsServiceFeignClient() {
            @Override
            public ResponseEntity<List<C7nAppServiceVO>> listAppServiceByActive(Long projectId) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nAppServiceVO>> pageAppServiceByOptions(Long projectId, Boolean doPage, Integer page, Integer size, String params) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nAppServiceVO>> listOrPageProjectAppServices(Long projectId, Set<Long> ids, Boolean doPage, Integer page, Integer size) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<Page<C7nAppServiceVO>> listAppServiceByIds(Set<Long> ids) {
                throw new CommonException("error.feign.fallback");
            }

            @Override
            public ResponseEntity<List<C7nGlUserVO>> listUsersByGitlabUserIds(Set<Integer> gitlabUserIds) {
                throw new CommonException("error.feign.fallback");
            }
        };
    }
}
