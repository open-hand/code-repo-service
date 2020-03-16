package org.hrds.rducm.gitlab.infra.feign;

import org.hrds.rducm.gitlab.infra.feign.vo.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/16
 */
@FeignClient(value = "base-service")
public interface BaseFeign {
    /**
     * 查询用户信息
     *
     * @param organizationId organizationId
     * @param id             id
     * @return UserDTO
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/users/{id}")
    ResponseEntity<UserDTO> query(@PathVariable(name = "organization_id") Long organizationId,
                                  @PathVariable("id") Long id);
}
