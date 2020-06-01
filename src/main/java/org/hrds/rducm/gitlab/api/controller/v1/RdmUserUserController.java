package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
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
@RestController("rdmUserUserController.v1")
@RequestMapping("/v1/gitlab/users")
public class RdmUserUserController extends BaseController {
    @Autowired
    private RdmUserAppService rdmUserAppService;

    @ApiOperation(value = "查询个人Gitlab信息(用户层)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/self")
    public ResponseEntity<RdmUserViewDTO> queryUser() {
        return Results.success(rdmUserAppService.queryUserSelf());
    }
}