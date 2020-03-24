package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @ApiOperation(value = "查询代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "组织id", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "projectIds", value = "项目id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "appServiceName", value = "应用服务名称(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "用户名(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "loginName", value = "登录名(模糊)", paramType = "query", dataType = "String"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberViewDTO>> pageByOptions(@PathVariable Long organizationId,
                                                                    @RequestParam(required = false) Set<Long> projectIds,
                                                                    PageRequest pageRequest,
                                                                    RdmMemberQueryDTO query) {
//        rdmMemberAppService.pageByOptions(projectId, pageRequest, query);
        return Results.success();
    }
}
