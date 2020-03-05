package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmOperationLogService;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
@RestController("gitlabOperationLogController.v1")
@RequestMapping("/v1/{organizationId}/projects/{projectId}/gitlab/operation-logs")
public class RdmOperationLogController extends BaseController {

    @Autowired
    private RdmOperationLogService operationLogService;

    @ApiOperation(value = "查询成员管理操作日志列表")
    @Permission(level = ResourceLevel.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<Page<OperationLogViewDTO>> pageByOptionsMemberLog(@PathVariable Long organizationId,
                                                                            @PathVariable Long projectId,
                                                                            @SortDefault(value = RdmOperationLog.FIELD_CREATION_DATE,
                                                                                    direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                            OperationLogQueryDTO queryDTO) {

        Page<OperationLogViewDTO> list = operationLogService.pageByOptionsMemberLog(projectId, null, pageRequest, queryDTO);
        return Results.success(list);
    }


}
