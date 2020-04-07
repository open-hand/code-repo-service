package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuthDetailViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.vo.ExportParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ying.xie@hand-china.com
 */
//@Api(tags = SwaggerTags.RDM_MEMBER)
@RestController("rdmMemberProjController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab/repositories/members")
public class RdmMemberProjController extends BaseController {
    private final RdmMemberAppService rdmMemberAppService;

    @Autowired
    private IRdmMemberService iRdmMemberService;

    public RdmMemberProjController(RdmMemberAppService rdmMemberAppService) {
        this.rdmMemberAppService = rdmMemberAppService;
    }

    @ApiOperation(value = "查询代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "appServiceName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                    @PathVariable Long projectId,
                                                                    PageRequest pageRequest,
                                                                    RdmMemberQueryDTO query) {
        return Results.success(rdmMemberAppService.pageByOptions(projectId, pageRequest, query));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "rdmMemberBatchDTO", value = "body参数", dataType = "RdmMemberBatchDTO", required = true),
    })
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER}, permissionPublic = true)
    @PostMapping("/batch-add")
    public ResponseEntity<?> batchAddMembers(@PathVariable Long organizationId,
                                             @PathVariable Long projectId,
                                             @RequestBody RdmMemberBatchDTO rdmMemberBatchDTO) {
        validObject(rdmMemberBatchDTO);
        rdmMemberAppService.batchAddOrUpdateMembers(organizationId, projectId, rdmMemberBatchDTO);
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
    public ResponseEntity<Page<MemberExportDTO>> export(@PathVariable Long organizationId,
                                                        @PathVariable Long projectId,
                                                        PageRequest pageRequest,
                                                        RdmMemberQueryDTO query,
                                                        ExportParam exportParam,
                                                        HttpServletResponse response) {
        exportParam.setIds(Sets.newHashSet(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L));

        return Results.success(rdmMemberAppService.export(projectId, pageRequest, query, exportParam, response));
    }

    @ApiOperation(value = "查询成员授权情况")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/audit/security-audit")
    @ApiImplicitParams({

    })
    public ResponseEntity<PageInfo<MemberAuthDetailViewDTO>> pageSecurityAudit(@PathVariable Long organizationId,
                                                                               @PathVariable Long projectId,
                                                                               PageRequest pageRequest) {
        return Results.success(iRdmMemberService.pageMembersRepositoryAuthorized(organizationId, projectId, pageRequest));
    }
}
