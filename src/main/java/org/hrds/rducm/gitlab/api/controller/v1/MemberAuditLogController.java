package org.hrds.rducm.gitlab.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 成员权限审计日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@RestController("memberAuditLogController.v1")
@RequestMapping("/v1/{organizationId}/member-audit-logs")
public class MemberAuditLogController extends BaseController {

    @Autowired
    private MemberAuditLogRepository memberAuditLogRepository;

    @ApiOperation(value = "成员权限审计日志表列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<MemberAuditLog>> list(MemberAuditLog memberAuditLog, @ApiIgnore @SortDefault(value = MemberAuditLog.FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<MemberAuditLog> list = memberAuditLogRepository.pageAndSort(pageRequest, memberAuditLog);
        return Results.success(list);
    }

    @ApiOperation(value = "成员权限审计日志表明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<MemberAuditLog> detail(@PathVariable Long id) {
        MemberAuditLog memberAuditLog = memberAuditLogRepository.selectByPrimaryKey(id);
        return Results.success(memberAuditLog);
    }

    @ApiOperation(value = "创建成员权限审计日志表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<MemberAuditLog> create(@RequestBody MemberAuditLog memberAuditLog) {
        validObject(memberAuditLog);
        memberAuditLogRepository.insertSelective(memberAuditLog);
        return Results.success(memberAuditLog);
    }

    @ApiOperation(value = "修改成员权限审计日志表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<MemberAuditLog> update(@RequestBody MemberAuditLog memberAuditLog) {
        SecurityTokenHelper.validToken(memberAuditLog);
        memberAuditLogRepository.updateByPrimaryKeySelective(memberAuditLog);
        return Results.success(memberAuditLog);
    }

    @ApiOperation(value = "删除成员权限审计日志表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody MemberAuditLog memberAuditLog) {
        SecurityTokenHelper.validToken(memberAuditLog);
        memberAuditLogRepository.deleteByPrimaryKey(memberAuditLog);
        return Results.success();
    }

}
