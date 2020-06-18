package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuditRecordQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
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
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RdmMemberAuditRecordViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                           @PathVariable Long projectId,
                                                                           @RequestParam(required = false) Set<Long> repositoryIds,
                                                                           PageRequest pageRequest,
                                                                           MemberAuditRecordQueryDTO queryDTO) {
        return Results.success(iRdmMemberAuditRecordService.pageByOptions(organizationId, Collections.singleton(projectId), repositoryIds, pageRequest, queryDTO, ResourceLevel.ORGANIZATION));
    }

    /**
     * TODO 废弃
     * @param id
     * @param syncStrategy
     * @return
     */
    @ApiOperation(value = "同步(废弃)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{id}/sync")
    @Deprecated
    public ResponseEntity<?> sync(@Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMAR) @PathVariable Long id, @RequestParam int syncStrategy) {
        rdmMemberAuditAppService.syncByStrategy(id, syncStrategy);
        return Results.success();
    }

    @ApiOperation(value = "权限修复")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{id}/audit-fix")
    public ResponseEntity<?> auditFix(@PathVariable Long organizationId,
                                      @PathVariable Long projectId,
                                      @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMAR) @PathVariable Long id,
                                      @RequestParam Long repositoryId) {
        rdmMemberAuditAppService.auditFix(organizationId, projectId, repositoryId, id);
        return Results.success();
    }
}
