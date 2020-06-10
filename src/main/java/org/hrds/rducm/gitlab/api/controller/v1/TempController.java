package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.app.job.MemberInitJob;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * FIXME
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
    private RdmUserRepository rdmUserRepository;
    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private IMemberAuditService iMemberAuditService;

    @ApiOperation(value = "查询gitlab用户(平台层)")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @GetMapping("/users")
    public ResponseEntity<Object> queryUser(@RequestParam String username) {
        return Results.success(rdmUserRepository.getUserFromGitlab(username));
    }

    @ApiOperation(value = "处理过期成员(定时任务测试)")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @GetMapping("/handle-expired-members")
    public ResponseEntity<?> handleExpiredMembers() {
        rdmMemberAppService.handleExpiredMembers();
        return Results.success();
    }

    @ApiOperation(value = "批量新增代码库成员(项目层), saga测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "rdmMemberBatchDTO", value = "body参数", dataType = "RdmMemberBatchDTO", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION, permissionPublic = true)
    @PostMapping("/organizations/{organizationId}/projects/{projectId}/members/batch-add")
    public ResponseEntity<Object> batchAddMembersTemp(@PathVariable Long organizationId,
                                                      @PathVariable Long projectId,
                                                      @RequestBody RdmMemberBatchDTO rdmMemberBatchDTO) {
//        validObject(rdmMemberBatchDTO);
        rdmMemberAppService.batchAddMemberSagaDemo(organizationId, projectId, rdmMemberBatchDTO);
        return Results.created(null);
    }

    @Autowired
    private MemberInitJob memberInitJob;

    @ApiOperation(value = "上线初始化成员测试")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @PostMapping("/organizations/{organizationId}/init-members")
    public void initMembers(@PathVariable Long organizationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("organizationId", organizationId);
        memberInitJob.initRdmMembers(map);
    }

    // TODO 测试用,需删除,改为定时任务调用
    @ApiOperation(value = "对组织下所有成员进行权限审计(组织层) 测试用,需删除,改为定时任务调用")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/batch-audit")
    public ResponseEntity<?> batchAudit(@PathVariable Long organizationId) {
        iMemberAuditService.auditMembersByOrganizationId(organizationId);
        return Results.success();
    }
}
