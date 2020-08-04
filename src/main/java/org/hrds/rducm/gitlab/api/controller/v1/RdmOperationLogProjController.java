package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmOperationLogAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Set;

/**
 * 操作日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
//@Api(tags = SwaggerTags.RDM_OPERATION_LOG)
@RestController("rdmOperationLogController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab/repositories/operation-logs")
public class RdmOperationLogProjController extends BaseController {

    @Autowired
    private RdmOperationLogAppService operationLogService;

    @ApiOperation(value = "查询成员管理操作日志列表(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = ApiInfoConstants.REPOSITORY_ID, paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "opUserId", value = "操作人，用户id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "opEventTypes", value = "操作事件类型", paramType = "query", dataType = "String", allowMultiple = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<OperationLogViewDTO>> pageByOptionsMemberLog(@PathVariable Long projectId,
                                                                            @SortDefault(value = RdmOperationLog.FIELD_CREATION_DATE,
                                                                                    direction = Sort.Direction.DESC)
                                                                            @ApiIgnore PageRequest pageRequest,
                                                                            @Encrypt @RequestParam(required = false) Set<Long> repositoryIds,
                                                                            OperationLogQueryDTO queryDTO) {

        Page<OperationLogViewDTO> list = operationLogService.pageByOptionsMemberLog(projectId, repositoryIds, pageRequest, queryDTO);
        return Results.success(list);
    }


}
