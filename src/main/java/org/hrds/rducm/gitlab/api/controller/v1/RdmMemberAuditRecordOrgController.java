package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.annotation.Permission;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuditRecordQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 成员权限审计日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
@RestController("rdmMemberAuditRecordOrgController.v1")
@RequestMapping("/v1/organizations/{organizationId}/member-audit-records")
public class RdmMemberAuditRecordOrgController extends BaseController {
    @Autowired
    private IRdmMemberAuditRecordService iRdmMemberAuditRecordService;
    @Autowired
    private IMemberAuditService iMemberAuditService;

    @ApiOperation(value = "查询权限审计结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appServiceName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberAuditRecordViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                               @RequestParam(required = false) Set<Long> projectIds,
                                                                               @RequestParam(required = false) Set<Long> repositoryIds,
                                                                               PageRequest pageRequest,
                                                                               MemberAuditRecordQueryDTO queryDTO) {
        return Results.success(iRdmMemberAuditRecordService.pageByOptions(organizationId, projectIds, repositoryIds, pageRequest, queryDTO ,ResourceType.ORGANIZATION));
    }

    @ApiOperation(value = "对组织下所有成员进行权限审计")
    @Permission(type = ResourceType.ORGANIZATION, permissionPublic = true)
    @PostMapping("/batch-audit")
    public ResponseEntity<?> batchAudit(@PathVariable Long organizationId) {
        iMemberAuditService.auditMembersByOrganizationId(organizationId);
        return Results.success();
    }


}
