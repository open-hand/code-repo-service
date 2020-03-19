package org.hrds.rducm.gitlab.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
            public ResponseEntity<List<C7nAppServiceVO>> listRepositoriesByActive(Long projectId) {
                return null;
            }

            @Override
            public ResponseEntity<PageInfo<C7nAppServiceVO>> pageProjectAppServiceByIds(Long projectId, Set<Long> ids, Boolean doPage, Boolean withVersion, Integer page, Integer size, String params) {
                return null;
            }

            @Override
            public ResponseEntity<PageInfo<C7nAppServiceVO>> listOrPageProjectAppServices(Long projectId, Set<Long> ids, Boolean doPage, Integer page, Integer size) {
                return null;
            }
        };
    }
}
