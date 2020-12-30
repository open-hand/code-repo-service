package org.hrds.rducm.gitlab.api.controller.v1;

import com.google.common.collect.Sets;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.*;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseUserQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.export.MemberExportDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.vo.ExportParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "params", value = "通用查询参数(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RdmMemberViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                @PathVariable Long projectId,
                                                                PageRequest pageRequest,
                                                                @Encrypt RdmMemberQueryDTO query) {
        return Results.success(rdmMemberAppService.pageByOptions(projectId, pageRequest, query));
    }

    @ApiOperation(value = "列表查询代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "params", value = "通用查询参数(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enabled", value = "用户启用标志", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "syncGitlabFlag", value = "Gitlab同步标识", paramType = "query", dataType = "Boolean"),
            @ApiImplicitParam(name = "glExpiresFlag", value = "Gitlab过期标识", paramType = "query", dataType = "Boolean"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<List<RdmMemberViewDTO>> listByOptions(@PathVariable Long organizationId,
                                                                @PathVariable Long projectId,
                                                                @Encrypt RdmMemberQueryDTO query) {
        return Results.success(rdmMemberAppService.listByOptions(projectId,  query));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "rdmMemberBatchDTO", value = "body参数", dataType = "RdmMemberBatchDTO", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.PROJECT_OWNER})
    @PostMapping("/batch-add")
    public ResponseEntity<?> batchAddMembers(@PathVariable Long organizationId,
                                             @PathVariable Long projectId,
                                             @RequestBody RdmMemberBatchDTO rdmMemberBatchDTO) {
        validObject(rdmMemberBatchDTO);
        rdmMemberAppService.batchAddOrUpdateMembers(organizationId, projectId, rdmMemberBatchDTO);
        return Results.created(null);
    }

    @ApiOperation(value = "权限导出")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "repositoryName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/audit/security-audit")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
    })
    public ResponseEntity<Page<MemberAuthDetailViewDTO>> pageSecurityAudit(@PathVariable Long organizationId,
                                                                           @PathVariable Long projectId,
                                                                           PageRequest pageRequest,
                                                                           BaseUserQueryDTO queryDTO) {
        return Results.success(iRdmMemberService.pageMembersRepositoryAuthorized(organizationId, projectId, pageRequest, queryDTO));
    }

    /**
     * 获取当前用户指定应用服务的代码库权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryIds
     * @return
     */
    @ApiOperation(value = "获取当前用户指定应用服务的代码库权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/self/privilege")
    public ResponseEntity<List<MemberPrivilegeViewDTO>> selfPrivilege(@PathVariable Long organizationId,
                                                                      @PathVariable Long projectId,
                                                                      @Encrypt @RequestBody Set<Long> repositoryIds) {
        return Results.success(iRdmMemberService.selfPrivilege(organizationId, projectId, repositoryIds));

    }

    /**
     * 启用/禁用应用服务同步代码库成员权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @param active
     * @return
     */
    @ApiOperation(value = "启用/禁用应用服务同步代码库成员权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch-valid")
    public ResponseEntity<List<RdmMember>> batchValid(@PathVariable Long organizationId,
                                                      @PathVariable Long projectId,
                                                      @RequestParam Long repositoryId,
                                                      @RequestParam Boolean active) {
        if (active) {
            return Results.success(rdmMemberAppService.batchValidMember(organizationId, projectId, repositoryId));
        } else {
            return Results.success(rdmMemberAppService.batchInvalidMember(organizationId, projectId, repositoryId));
        }
    }

    @ApiOperation(value = "批量移除代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberIds", value = "body参数", dataType = "Long", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/batch-remove")
    public ResponseEntity<?> batchRemoveMember(@PathVariable Long organizationId,
                                               @PathVariable Long projectId,
                                               @Encrypt @RequestBody Set<Long> memberIds) {
        rdmMemberAppService.batchRemoveMembers(organizationId, projectId, memberIds);
        return Results.success();
    }
}
