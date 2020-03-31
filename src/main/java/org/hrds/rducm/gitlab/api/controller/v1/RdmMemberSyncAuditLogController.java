package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberSyncLogService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成员权限审计日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
@RestController("memberSyncAuditLogController.v1")
@RequestMapping("/v1/organizations/{organizationId}/member-sync-audit-logs")
public class RdmMemberSyncAuditLogController extends BaseController {

    @Autowired
    private IRdmMemberSyncLogService rdmMemberSyncLogService;

    @ApiOperation(value = "")
    @Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
    @PostMapping
    public ResponseEntity<?> batchAudit(@PathVariable Long organizationId) {
        rdmMemberSyncLogService.batchCompare(organizationId);
        return Results.success();
    }


}
