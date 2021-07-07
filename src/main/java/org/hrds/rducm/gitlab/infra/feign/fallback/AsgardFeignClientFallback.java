package org.hrds.rducm.gitlab.infra.feign.fallback;

import java.util.List;
import org.hrds.rducm.gitlab.infra.feign.AsgardFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.SagaInstanceDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;


/**
 * @author dengyouquan
 **/
@Component
public class AsgardFeignClientFallback implements AsgardFeignClient {

    @Override
    public ResponseEntity<List<SagaInstanceDetails>> queryByRefTypeAndRefIds(String refType, List<String> refIds, String sagaCode) {
        throw new CommonException("error.query.instance.detail");
    }

}