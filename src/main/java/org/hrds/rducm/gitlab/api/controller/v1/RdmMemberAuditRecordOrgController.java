package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
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
@RestController("rdmMemberAuditRecordOrgController.v1")
@RequestMapping("/v1/organizations/{organizationId}/member-audit-records")
public class RdmMemberAuditRecordOrgController extends BaseController {
    @Autowired
    private IRdmMemberAuditRecordService rdmMemberSyncLogService;
    @Autowired
    private IMemberAuditService iMemberAuditService;

    @ApiOperation(value = "对组织下所有成员进行权限审计")
    @Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
    @PostMapping
    public ResponseEntity<?> batchAudit(@PathVariable Long organizationId) {
        iMemberAuditService.auditMembersByOrganizationId(organizationId);
        return Results.success();
    }


}
