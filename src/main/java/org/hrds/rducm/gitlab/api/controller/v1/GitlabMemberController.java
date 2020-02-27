package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberUpdateDTO;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ying.xie@hand-china.com
 */
//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabMemberController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/members")
public class GitlabMemberController extends BaseController {
    private final GitlabMemberService gitlabMemberService;

    public GitlabMemberController(GitlabMemberService gitlabMemberService) {
        this.gitlabMemberService = gitlabMemberService;
    }

    @ApiOperation(value = "修改代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true),
    })
    @ApiParam(name = "gitlabMemberUpdateDTO", value = "参数")
    @Permission(permissionPublic = true)
    @PutMapping("/{memberId}")
    public ResponseEntity<Object> updateMember(@PathVariable Long projectId,
                                               @PathVariable Long repositoryId,
                                               @PathVariable Long memberId,
                                               @RequestBody GitlabMemberUpdateDTO gitlabMemberUpdateDTO) {
        validObject(gitlabMemberUpdateDTO);
        gitlabMemberService.updateMember(projectId, repositoryId, memberId, gitlabMemberUpdateDTO);
        return Results.created(null);
    }

    @ApiOperation(value = "移除仓库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true)
    })
    @Permission(permissionPublic = true)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Object> removeMember(@PathVariable Long projectId,
                                               @PathVariable Long repositoryId,
                                               @PathVariable Long memberId) {
        gitlabMemberService.removeMember(projectId, repositoryId, memberId);
        return Results.created(null);
    }
}
