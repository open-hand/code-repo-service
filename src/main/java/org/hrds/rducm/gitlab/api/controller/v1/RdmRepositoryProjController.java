package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nAppServiceViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmRepositoryAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

//@Api(tags = SwaggerTags.RDM_REPOSITORY)
@RestController("rdmRepositoryProjController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab/repositories")
public class RdmRepositoryProjController extends BaseController {
    @Autowired
    private RdmRepositoryAppService rdmRepositoryAppService;
    @Autowired
    private IRdmRepositoryService rdmRepositoryService;

    @ApiOperation(value = "查询所有已经启用的服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list-by-active")
    public ResponseEntity<List<BaseC7nAppServiceViewDTO>> listByActive(@PathVariable(value = "projectId") Long projectId,
                                                                       @RequestParam(required = false) String condition) {
        return ResponseEntity.ok(rdmRepositoryAppService.listByActive(projectId, condition));
    }

    @ApiOperation(value = "查询项目总览(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/overview")
    public ResponseEntity<Page<RepositoryOverViewDTO>> pageOverviewByOptions(@PathVariable Long projectId,
                                                                             PageRequest pageRequest,
                                                                             @Encrypt @RequestParam(required = false) Set<Long> repositoryIds) {
        Page<RepositoryOverViewDTO> repositoryOverViewDTOS = rdmRepositoryService.pageOverviewByOptions(projectId, pageRequest, repositoryIds);
        return Results.success(repositoryOverViewDTOS);
    }
}
