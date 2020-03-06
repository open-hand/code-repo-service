package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 临时Controller, 里面的接口需删除
 * 用于存放定时任务等的测试接口
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
@RestController
@RequestMapping("/v1/gitlab/temp")
public class TempController extends BaseController {

    @Autowired
    private RdmMemberService rdmMemberService;

    @ApiOperation(value = "处理过期成员")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping("/handle-expired-members")
    public ResponseEntity<?> handleExpiredMembers() {
        rdmMemberService.handleExpiredMembers();
        return Results.success();
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "rdmMemberBatchDTO", value = "body参数", dataType = "RdmMemberBatchDTO", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/projects/{projectId}/members/batch-add")
    public ResponseEntity<Object> batchAddMembersTemp(@PathVariable Long projectId,
                                                  @RequestBody RdmMemberBatchDTO rdmMemberBatchDTO) {
        validObject(rdmMemberBatchDTO);
        rdmMemberService.batchAddMemberSagaDemo(projectId, rdmMemberBatchDTO);
        return Results.created(null);
    }
}
