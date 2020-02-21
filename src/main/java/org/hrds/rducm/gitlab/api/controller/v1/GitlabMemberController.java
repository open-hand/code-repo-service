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
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabMemberController.v1")
@RequestMapping("rducm/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/members")
public class GitlabMemberController extends BaseController {
    private final GitlabMemberService gitlabMemberService;

    public GitlabMemberController(GitlabMemberService gitlabMemberService) {
        this.gitlabMemberService = gitlabMemberService;
    }

    @ApiOperation(value = "修改成员")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
    })
    @PutMapping("/{memberId}")
    public ResponseEntity<Object> updateMember(@PathVariable Long repositoryId,
                                               @PathVariable Long memberId,
                                               @RequestBody GitlabMember gitlabMember) {
        gitlabMemberService.updateMember(gitlabMember);
        return Results.created(null);
    }

    @ApiOperation(value = "移除成员")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
    })
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Object> removeMember(@PathVariable Long repositoryId,
                                               @PathVariable Long memberId,
                                               @RequestBody GitlabMember gitlabMember) {
        gitlabMemberService.removeMember(memberId, gitlabMember.getGlProjectId(), gitlabMember.getGlUserId());
        return Results.created(null);
    }
}
