package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/7
 */
@RestController("rdmProjectController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab")
public class RdmProjectController extends BaseController {
    @Autowired
    private IRdmMemberService iRdmMemberService;

    @ApiOperation(value = "查询成员所有应用服务的权限")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/members/{userId}/permissions")
    @ApiImplicitParams({

    })
    public ResponseEntity<PageInfo<RdmMemberViewDTO>> pageMemberPermissions(@PathVariable Long organizationId,
                                                                            @PathVariable Long projectId,
                                                                            @PathVariable Long userId,
                                                                            PageRequest pageRequest) {
        return Results.success(iRdmMemberService.pageMemberPermissions(organizationId, projectId, userId, pageRequest));
    }
}
