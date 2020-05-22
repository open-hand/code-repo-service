package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 成员权限审计日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@RestController("memberAuditLogOrgController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/gitlab/member-audit-logs")
public class MemberAuditLogOrgController extends BaseController {
    @Autowired
    private IMemberAuditService iMemberAuditService;

    @ApiOperation(value = "获取最新一条审计日志(组织层)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/latest")
    public ResponseEntity<MemberAuditLog> detailLatest(@PathVariable Long organizationId,
                                                       @RequestParam(required = false) Long projectId) {
        MemberAuditLog log = iMemberAuditService.detailLatestAuditLog(organizationId, projectId);
        return Results.success(log);
    }

}
