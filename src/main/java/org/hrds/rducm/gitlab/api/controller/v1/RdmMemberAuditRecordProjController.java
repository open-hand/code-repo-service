package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuditRecordQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

/**
 * 成员权限审计日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
@RestController("rdmMemberAuditRecordProjController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/member-audit-records")
public class RdmMemberAuditRecordProjController extends BaseController {
    @Autowired
    private IRdmMemberAuditRecordService iRdmMemberAuditRecordService;
    @Autowired
    private RdmMemberAuditAppService rdmMemberAuditAppService;

    @ApiOperation(value = "查询权限审计结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appServiceName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberAuditRecordViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                               @PathVariable Long projectId,
                                                                               @RequestParam(required = false) Set<Long> repositoryIds,
                                                                               PageRequest pageRequest,
                                                                               MemberAuditRecordQueryDTO queryDTO) {
        return Results.success(iRdmMemberAuditRecordService.pageByOptions(organizationId, Collections.singleton(projectId), repositoryIds, pageRequest, queryDTO, ResourceType.PROJECT));
    }

    @ApiOperation(value = "同步")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{id}/sync")
    public ResponseEntity<?> sync(@PathVariable Long id, @RequestParam int syncStrategy) {
        rdmMemberAuditAppService.syncByStrategy(id, syncStrategy);
        return Results.success();
    }
}
