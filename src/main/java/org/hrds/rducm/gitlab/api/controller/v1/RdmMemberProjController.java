package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ying.xie@hand-china.com
 */
//@Api(tags = SwaggerTags.RDM_MEMBER)
@RestController("rdmMemberProjController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/members")
public class RdmMemberProjController extends BaseController {
    private final RdmMemberAppService rdmMemberAppService;

    public RdmMemberProjController(RdmMemberAppService rdmMemberAppService) {
        this.rdmMemberAppService = rdmMemberAppService;
    }

    @ApiOperation(value = "查询代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "appServiceName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberViewDTO>> pageByOptions(@PathVariable Long projectId,
                                                                    PageRequest pageRequest,
                                                                    RdmMemberQueryDTO query) {
        return Results.success(rdmMemberAppService.pageByOptions(projectId, pageRequest, query));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "rdmMemberBatchDTO", value = "body参数", dataType = "RdmMemberBatchDTO", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/batch-add")
    public ResponseEntity<?> batchAddMembers(@PathVariable Long projectId,
                                             @RequestBody RdmMemberBatchDTO rdmMemberBatchDTO) {
        validObject(rdmMemberBatchDTO);
        rdmMemberAppService.batchAddOrUpdateMembers(projectId, rdmMemberBatchDTO);
        return Results.created(null);
    }

    @ApiOperation(value = "权限导出")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/export")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "appServiceName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "exportType", value = "导出类型", paramType = "query", dataType = "String", defaultValue = "DATA", required = true),
    })
    public ResponseEntity<Page<MemberExportDTO>> projectExport(@PathVariable Long projectId,
                                                               PageRequest pageRequest,
                                                               RdmMemberQueryDTO query,
                                                               ExportParam exportParam,
                                                               HttpServletResponse response) {
        exportParam.setIds(Sets.newHashSet(1L, 2L, 3L, 4L));

        return Results.success(rdmMemberAppService.export(projectId, pageRequest, query, exportParam, response));
    }
}
