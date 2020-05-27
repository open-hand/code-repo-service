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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成员权限审计日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@RestController("memberAuditLogController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab/member-audit-logs")
public class MemberAuditLogController extends BaseController {
    @Autowired
    private IMemberAuditService iMemberAuditService;

    @ApiOperation(value = "获取最新一条审计日志")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/detail/latest")
    public ResponseEntity<MemberAuditLog> detailLatest(@PathVariable Long organizationId,
                                                       @PathVariable Long projectId) {
        MemberAuditLog log = iMemberAuditService.detailLatestAuditLog(organizationId, projectId);
        return Results.success(log);
    }

}
