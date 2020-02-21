package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabPermissionController.v1")
@RequestMapping("rducm/v1/gitlab/projects/{projectId}")
public class ProjectController {
    private final GitlabMemberService gitlabMemberService;

    public ProjectController(GitlabMemberService gitlabMemberService) {
        this.gitlabMemberService = gitlabMemberService;
    }

    @ApiOperation(value = "查询项目下成员")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
    })
    @PostMapping("/members")
    public ResponseEntity<List<GitlabMember>> pageByOptions(@PathVariable Long projectId) {
        return Results.success(gitlabMemberService.list(projectId));
    }

    @ApiOperation(value = "批量新增成员")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
    })
    @PostMapping("/members/batch-add")
    public ResponseEntity<Object> batchAddMembers(@PathVariable Long repositoryId,
                                                  @RequestBody List<GitlabMember> gitlabMembers) {
        gitlabMemberService.batchAddMembers(gitlabMembers);
        return Results.created(null);
    }
}
