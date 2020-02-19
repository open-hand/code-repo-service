package org.hrds.rducm.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;


/**
 * FeignDemo
 */
@FeignClient(value = "demo-service", path = "/v1/demos")
public interface DemoFeign {


}
