package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.app.service.GitlabBranchService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabBranchController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/branches")
public class GitlabBranchController extends BaseController {
    @Autowired
    private GitlabBranchService gitlabBranchService;

    @ApiOperation(value = "查询保护分支")
    @Permission(permissionPublic = true)
    @GetMapping("/protected-branches")
    public ResponseEntity<List<ProtectedBranch>> getProtectedBranches(@PathVariable Long projectId,
                                                                      @PathVariable Long repositoryId,
                                                                      @RequestParam Integer glProjectId) {
        return Results.success(gitlabBranchService.getProtectedBranches(glProjectId));
    }

    @ApiOperation(value = "添加保护分支")
    @Permission(permissionPublic = true)
    @PostMapping("/protected-branches")
    public ResponseEntity<ProtectedBranch> createProtectedBranch(@PathVariable Long projectId,
                                                                 @PathVariable Long repositoryId,
                                                                 @RequestParam Integer glProjectId,
                                                                 @RequestParam String branchName,
                                                                 @RequestParam Integer pushAccessLevel,
                                                                 @RequestParam Integer mergeAccessLevel) {
        return Results.success(gitlabBranchService.protectBranch(glProjectId, branchName, pushAccessLevel, mergeAccessLevel));
    }

    @ApiOperation(value = "修改保护分支")
    @Permission(permissionPublic = true)
    @PutMapping("/protected-branches")
    public ResponseEntity<ProtectedBranch> updateProtectedBranch(@PathVariable Long projectId,
                                                                 @PathVariable Long repositoryId,
                                                                 @RequestParam Integer glProjectId,
                                                                 @RequestParam String branchName,
                                                                 @RequestParam Integer pushAccessLevel,
                                                                 @RequestParam Integer mergeAccessLevel) {
        return Results.success(gitlabBranchService.protectBranch(glProjectId, branchName, pushAccessLevel, mergeAccessLevel));
    }

    @ApiOperation(value = "删除保护分支")
    @Permission(permissionPublic = true)
    @DeleteMapping("/protected-branches")
    public ResponseEntity<ProtectedBranch> updateProtectedBranch(@PathVariable Long projectId,
                                                                 @PathVariable Long repositoryId,
                                                                 @RequestParam Integer glProjectId,
                                                                 @RequestParam String branchName) {
        gitlabBranchService.unprotectBranch(glProjectId, branchName);
        return Results.created(null);
    }
}
