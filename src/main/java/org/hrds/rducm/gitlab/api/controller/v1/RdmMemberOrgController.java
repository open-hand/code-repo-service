package org.hrds.rducm.gitlab.api.controller.v1;

import com.google.common.collect.Sets;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.vo.ExportParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/24
 */
@RestController("rdmMemberOrgController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/gitlab/repositories/members")
public class RdmMemberOrgController extends BaseController {
    private final RdmMemberAppService rdmMemberAppService;

    public RdmMemberOrgController(RdmMemberAppService rdmMemberAppService) {
        this.rdmMemberAppService = rdmMemberAppService;
    }

    @ApiOperation(value = "查询代码库成员(组织层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectIds", value = "项目id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RdmMemberViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                PageRequest pageRequest,
                                                                RdmMemberQueryDTO query) {
        return Results.success(rdmMemberAppService.pageByOptionsOnOrg(organizationId, pageRequest, query));
    }

    @ApiOperation(value = "权限导出(组织层)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "exportType", value = "导出类型", paramType = "query", dataType = "String", defaultValue = "DATA", required = true),
    })
    public ResponseEntity<Page<MemberExportDTO>> export(@PathVariable Long organizationId,
                                                        PageRequest pageRequest,
                                                        RdmMemberQueryDTO query,
                                                        ExportParam exportParam,
                                                        HttpServletResponse response) {
        exportParam.setIds(Sets.newHashSet(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L));

        return Results.success(rdmMemberAppService.exportOnOrg(organizationId, pageRequest, query, exportParam, response));
    }
}
