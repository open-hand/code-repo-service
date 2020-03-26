package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.oauth.DetailsHelper;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.domain.service.impl.RdmMemberSyncLogServiceImpl;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hzero.core.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * todo 需删除
 */
@RestController
@RequestMapping("/v1/gitlab/test")
public class TestController extends BaseController {
    @Autowired
    private Gitlab4jClientWrapper gitlab4jClientWrapper;
//
//    @Autowired
//    private RdmMemberSyncLogServiceImpl securityAudit;
//
//    @Autowired
//    private BaseServiceFeignClient baseServiceFeignClient;

    static Logger logger = LoggerFactory.getLogger(TestController.class);

    @ApiOperation(value = "查询")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping("/users")
    public ResponseEntity<List<C7nUserVO>> queryUser() {
        Long userId = DetailsHelper.getUserDetails().getUserId();

        logger.warn("-------------------- getUserDetails:{}", DetailsHelper.getUserDetails());
        logger.warn("-------------------- userId:{}", userId);
//        return baseServiceFeignClient.listUsersByIds(ids, null);
        return null;
    }


}
