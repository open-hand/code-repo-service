package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberUpdateDTO;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ying.xie@hand-china.com
 */
@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabMemberController.v1")
@RequestMapping("rducm/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/members")
public class GitlabMemberController extends BaseController {
    private final GitlabMemberService gitlabMemberService;

    public GitlabMemberController(GitlabMemberService gitlabMemberService) {
        this.gitlabMemberService = gitlabMemberService;
    }

    @ApiOperation(value = "修改仓库成员")
    @Permission(permissionPublic = true)
    @PutMapping("/{memberId}")
    public ResponseEntity<Object> updateMember(@PathVariable Long projectId,
                                               @PathVariable Long repositoryId,
                                               @PathVariable Long memberId,
                                               @RequestBody GitlabMemberUpdateDTO gitlabMemberUpdateDTO) {
        gitlabMemberService.updateMember(projectId, repositoryId, memberId, gitlabMemberUpdateDTO);
        return Results.created(null);
    }

    @ApiOperation(value = "移除仓库成员")
    @Permission(permissionPublic = true)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Object> removeMember(@PathVariable Long repositoryId,
                                               @PathVariable Long memberId,
                                               @RequestBody @Valid GitlabMember gitlabMember) {
        gitlabMemberService.removeMember(memberId, gitlabMember.getGlProjectId(), gitlabMember.getGlUserId());
        return Results.created(null);
    }
}
