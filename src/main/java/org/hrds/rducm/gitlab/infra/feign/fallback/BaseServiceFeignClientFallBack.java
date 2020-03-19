package org.hrds.rducm.gitlab.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
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
public class BaseServiceFeignClientFallBack implements BaseServiceFeignClient {
    @Override
    public ResponseEntity<List<C7nUserVO>> listProjectUsersByName(Long projectId, String param) {
        throw new CommonException("error.feign.base.service");
    }

    @Override
    public ResponseEntity<PageInfo<C7nUserVO>> pageUsersByOptionsOnProjectLevel(Long projectId, int page, int size, String loginName, String realName) {
        throw new CommonException("error.feign.base.service");
    }

    @Override
    public ResponseEntity<List<C7nUserVO>> listProjectUsersByIds(Long projectId, Set<Long> userIds) {
        throw new CommonException("error.feign.base.service");
    }
}
