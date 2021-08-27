package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import java.util.Objects;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuditRecordQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.app.eventhandler.gitlab.GitlabPermissionRepair;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hrds.rducm.gitlab.infra.feign.vo.SagaInstanceDetails;
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
    @Autowired
    private Map<String, GitlabPermissionRepair> permissionRepairMap;
    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;

    @ApiOperation(value = "查询权限审计结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RdmMemberAuditRecordViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                           @PathVariable Long projectId,
                                                                           @Encrypt @RequestParam(required = false) Set<Long> repositoryIds,
                                                                           PageRequest pageRequest,
                                                                           MemberAuditRecordQueryDTO queryDTO) {
        return Results.success(iRdmMemberAuditRecordService.pageByOptions(organizationId, Collections.singleton(projectId), repositoryIds, pageRequest, queryDTO, ResourceLevel.ORGANIZATION));
    }

    @ApiOperation(value = "权限修复")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{id}/audit-fix")
    public ResponseEntity<?> auditFix(@PathVariable Long organizationId,
                                      @PathVariable Long projectId,
                                      @Encrypt @PathVariable Long id,
                                      @Encrypt @RequestParam Long repositoryId) {
        RdmMemberAuditRecord memberAuditRecord = rdmMemberAuditRecordRepository.selectByPrimaryKey(id);
        if (!Objects.isNull(memberAuditRecord)) {
            permissionRepairMap.get(memberAuditRecord.getType() + "GitlabPermissionRepair").gitlabPermissionRepair(memberAuditRecord);
        }
        return Results.success();
    }


    @ApiOperation(value = "权限批量修复")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch/audit-fix")
    public ResponseEntity<?> batchAuditFix(@PathVariable Long organizationId,
                                           @PathVariable Long projectId,
                                           @Encrypt @RequestBody Set<Long> recordIds) {
        rdmMemberAuditAppService.batchAuditFix(organizationId, projectId, recordIds);
        return Results.success();
    }


    @ApiOperation(value = "项目下权限审计")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/audit")
    public ResponseEntity<?> projectAudit(@PathVariable Long organizationId,
                                          @PathVariable Long projectId) {
        rdmMemberAuditAppService.projectAudit(organizationId, projectId);
        return Results.success();
    }

    //请求审计的执行状态
    @ApiOperation(value = "请求审计的执行状态")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/audit/status")
    public ResponseEntity<SagaInstanceDetails> projectAuditStatus(@PathVariable Long organizationId,
                                                                  @PathVariable Long projectId) {

        return Results.success(rdmMemberAuditAppService.projectAuditStatus(organizationId, projectId));
    }


    @ApiOperation(value = "请求权限修复的执行状态")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/audit/fix/status")
    public ResponseEntity<SagaInstanceDetails> projectAuditFixStatus(@PathVariable Long organizationId,
                                                                     @PathVariable Long projectId) {

        return Results.success(rdmMemberAuditAppService.projectAuditFixStatus(organizationId, projectId));
    }


}
