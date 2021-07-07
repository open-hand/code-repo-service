package org.hrds.rducm.gitlab.infra.feign.operate;

import java.util.ArrayList;
import java.util.List;
import org.hrds.rducm.gitlab.infra.feign.AsgardFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.SagaInstanceDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;

@Component
public class AsgardServiceClientOperator {

    @Autowired
    private AsgardFeignClient asgardFeignClient;

    public List<SagaInstanceDetails> queryByRefTypeAndRefIds(String refType, List<String> refIds, String sagaCode) {
        ResponseEntity<List<SagaInstanceDetails>> listResponseEntity;
        try {
            listResponseEntity = asgardFeignClient.queryByRefTypeAndRefIds(refType, refIds, sagaCode);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        if (listResponseEntity == null) {
            return new ArrayList<>();
        }
        return listResponseEntity.getBody();
    }

}
