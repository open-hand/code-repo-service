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
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmRepositoryAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmRepositoryService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.RDM_REPOSITORY)
@RestController("rdmRepositoryProjController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories")
public class RdmRepositoryProjController extends BaseController {
    @Autowired
    private RdmRepositoryAppService rdmRepositoryAppService;
    @Autowired
    private IRdmRepositoryService rdmRepositoryService;

    @ApiOperation(value = "查询所有已经启用的服务")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/list_by_active")
    public ResponseEntity<List<RdmRepository>> listByActive(@PathVariable(value = "projectId") Long projectId) {
        return ResponseEntity.ok(rdmRepositoryAppService.listByActive(projectId));
    }

    @ApiOperation(value = "查询项目总览(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "应用服务id", paramType = "query", dataType = "Long", allowMultiple = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/overview")
    public ResponseEntity<PageInfo<RepositoryOverViewDTO>> pageOverviewByOptions(@PathVariable Long projectId,
                                                                                 PageRequest pageRequest,
                                                                                 @RequestParam(required = false) List<Long> repositoryIds) {
        PageInfo<RepositoryOverViewDTO> repositoryOverViewDTOS = rdmRepositoryService.pageOverviewByOptions(projectId, pageRequest, repositoryIds);
        return Results.success(repositoryOverViewDTOS);
    }
}
