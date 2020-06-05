package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.api.controller.dto.RdmUserViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmUserAppService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API接口
 */
//@Api(tags = SwaggerTags.RDM_USER)
@RestController("rdmUserSiteController.v1")
@RequestMapping("/v1/gitlab/users")
public class RdmUserSiteController extends BaseController {
    @Autowired
    private RdmUserAppService rdmUserAppService;

    @ApiOperation(value = "新建用户(平台层)")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @PostMapping("/{userId}")
    public ResponseEntity<Object> createUser(@PathVariable Long userId,
                                             @RequestParam String glEmail,
                                             @RequestParam String glUsername,
                                             @RequestParam String glName) {
        rdmUserAppService.createUserWithRandomPassword(userId, glEmail, glUsername, glName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}