package org.hrds.rducm.gitlab.infra.feign;

import java.util.List;
import org.hrds.rducm.gitlab.infra.feign.fallback.AsgardFeignClientFallback;
import org.hrds.rducm.gitlab.infra.feign.vo.SagaInstanceDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




/**
 * @author dengyouquan
 **/
@FeignClient(value = "choerodon-asgard",
        fallback = AsgardFeignClientFallback.class)
public interface AsgardFeignClient {

    @GetMapping("/v1/sagas/instances/ref/business/instance")
    ResponseEntity<List<SagaInstanceDetails>> queryByRefTypeAndRefIds(@RequestParam(value = "refType") String refType,
                                                                      @RequestParam(value = "refIds") List<String> refIds,
                                                                      @RequestParam(value = "sagaCode") String sagaCode);

}