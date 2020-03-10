package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmOperationLogAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 操作日志表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
//@Api(tags = SwaggerTags.RDM_OPERATION_LOG)
@RestController("rdmOperationLogController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/operation-logs")
public class RdmOperationLogController extends BaseController {

    @Autowired
    private RdmOperationLogAppService operationLogService;

    @ApiOperation(value = "查询成员管理操作日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "opUserId", value = "操作人，用户id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", paramType = "query", dataType = "Date"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<OperationLogViewDTO>> pageByOptionsMemberLog(@PathVariable Long projectId,
                                                                                @SortDefault(value = RdmOperationLog.FIELD_CREATION_DATE,
                                                                                        direction = Sort.Direction.DESC)
                                                                                @ApiIgnore PageRequest pageRequest,
                                                                                OperationLogQueryDTO queryDTO) {

        PageInfo<OperationLogViewDTO> list = operationLogService.pageByOptionsMemberLog(projectId, null, pageRequest, queryDTO);
        return Results.success(list);
    }


}
