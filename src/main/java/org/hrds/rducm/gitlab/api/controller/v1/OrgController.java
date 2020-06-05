package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hzero.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/19
 */
@RestController("orgController.v1")
@RequestMapping("/v1/organizations/{organizationId}")
public class OrgController extends BaseController {
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    @ApiOperation(value = "查询组织下用户的项目列表(组织层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "path", required = true),
            @ApiImplicitParam(name = "name", value = "项目名称模糊搜索", paramType = "query"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<List<C7nProjectVO>> listProjectsByUserId(@PathVariable Long organizationId,
                                                                   @PathVariable Long userId,
                                                                   @RequestParam(required = false) String name) {
        List<C7nProjectVO> c7nProjectVOS = c7NBaseServiceFacade.listProjectsByUserIdOnOrgLevel(organizationId, userId, name);
        return ResponseEntity.ok(c7nProjectVOS);
    }
}
