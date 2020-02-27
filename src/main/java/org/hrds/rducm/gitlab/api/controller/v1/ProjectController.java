package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberBatchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberViewDTO;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("projectController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab")
public class ProjectController extends BaseController {
    private final GitlabMemberService gitlabMemberService;

    public ProjectController(GitlabMemberService gitlabMemberService) {
        this.gitlabMemberService = gitlabMemberService;
    }

    @ApiOperation(value = "查询代码库成员(项目层)")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/members")
    public ResponseEntity<Page<GitlabMemberViewDTO>> pageByOptions(@PathVariable Long projectId,
                                                                   PageRequest pageRequest,
                                                                   GitlabMemberViewDTO query) {
        return Results.success(gitlabMemberService.list(projectId, pageRequest));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "path", required = true),
            @ApiImplicitParam(name = "gitlabMembers", value = "新增成员信息", paramType = "GitlabMemberCreateDTO"),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/members/batch-add")
    public ResponseEntity<Object> batchAddMembers(@PathVariable Long projectId,
                                                  @RequestBody GitlabMemberBatchDTO gitlabMemberBatchDTO) {
        validObject(gitlabMemberBatchDTO);
        gitlabMemberService.batchAddOrUpdateMembers(projectId, gitlabMemberBatchDTO);
        return Results.created(null);
    }
}
