package org.hrds.rducm.gitlab.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.DevOpsServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
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
public class DevOpsServiceFeignClientFallBack implements DevOpsServiceFeignClient {

    @Override
    public ResponseEntity<List<C7nAppServiceVO>> listRepositoriesByActive(Long projectId) {
        throw new CommonException("error.feign.devops.service");
    }

    @Override
    public ResponseEntity<PageInfo<C7nAppServiceVO>> pageAppServiceByOptions(Long projectId, Boolean doPage, String params) {
        throw new CommonException("error.feign.devops.service");
    }

    @Override
    public ResponseEntity<PageInfo<C7nAppServiceVO>> pageProjectAppServiceByIds(Long projectId, Set<Long> ids, Boolean doPage) {
        throw new CommonException("error.feign.devops.service");
    }
}
