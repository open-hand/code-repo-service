package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ying.xie@hand-china.com
 */
@Api(tags = SwaggerTags.RDM_MEMBER)
@RestController("rdmMemberProjController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories")
public class RdmMemberProjController extends BaseController {
    private final RdmMemberService rdmMemberService;

    public RdmMemberProjController(RdmMemberService rdmMemberService) {
        this.rdmMemberService = rdmMemberService;
    }

    @ApiOperation(value = "查询代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/members")
    public ResponseEntity<PageInfo<RdmMemberViewDTO>> pageByOptions(@PathVariable Long projectId,
                                                                    PageRequest pageRequest,
                                                                    RdmMemberQueryDTO query) {
        return Results.success(rdmMemberService.pageByOptions(projectId, pageRequest, query));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "rdmMemberBatchDTO", value = "body参数", dataType = "RdmMemberBatchDTO", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/members/batch-add")
    public ResponseEntity<?> batchAddMembers(@PathVariable Long projectId,
                                             @RequestBody RdmMemberBatchDTO rdmMemberBatchDTO) {
        validObject(rdmMemberBatchDTO);
        rdmMemberService.batchAddOrUpdateMembers(projectId, rdmMemberBatchDTO);
        return Results.created(null);
    }
}
