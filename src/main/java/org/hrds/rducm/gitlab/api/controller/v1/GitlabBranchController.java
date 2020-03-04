package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.app.service.GitlabBranchService;
import org.hrds.rducm.gitlab.domain.repository.GitlabBranchRepository;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabBranchController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/branches")
public class GitlabBranchController extends BaseController {
    public static final String API_INFO_PUSH_ACCESS_LEVEL = "是否允许推送-权限级别(0|30|40)";
    public static final String API_INFO_MERGE_ACCESS_LEVEL = "是否允许合并-权限级别(0|30|40)";


    @Autowired
    private GitlabBranchService gitlabBranchService;

    @ApiOperation(value = "查询分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
    })
    @Permission(permissionPublic = true)
    @GetMapping
    public ResponseEntity<List<Branch>> getBranches(@PathVariable Long projectId,
                                                    @PathVariable Long repositoryId) {
        return Results.success(gitlabBranchService.getBranches(repositoryId));
    }

    @ApiOperation(value = "查询保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
    })
    @Permission(permissionPublic = true)
    @GetMapping("/protected-branches")
    public ResponseEntity<List<ProtectedBranch>> getProtectedBranches(@PathVariable Long projectId,
                                                                      @PathVariable Long repositoryId) {
        return Results.success(gitlabBranchService.getProtectedBranches(repositoryId));
    }

    @ApiOperation(value = "添加保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "branchName", value = "分支名(可使用通配符)", paramType = "query", required = true),
            @ApiImplicitParam(name = "pushAccessLevel", value = API_INFO_PUSH_ACCESS_LEVEL, paramType = "query", required = true),
            @ApiImplicitParam(name = "mergeAccessLevel", value = API_INFO_MERGE_ACCESS_LEVEL, paramType = "query", required = true),
    })
    @Permission(permissionPublic = true)
    @PostMapping("/protected-branches")
    public ResponseEntity<ProtectedBranch> createProtectedBranch(@PathVariable Long projectId,
                                                                 @PathVariable Long repositoryId,
                                                                 @RequestParam String branchName,
                                                                 @RequestParam Integer pushAccessLevel,
                                                                 @RequestParam Integer mergeAccessLevel) {
        return Results.success(gitlabBranchService.protectBranch(repositoryId, branchName, pushAccessLevel, mergeAccessLevel));
    }

    @ApiOperation(value = "修改保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "branchName", value = "分支名", paramType = "query", required = true),
            @ApiImplicitParam(name = "pushAccessLevel", value = API_INFO_PUSH_ACCESS_LEVEL, paramType = "query", required = true),
            @ApiImplicitParam(name = "mergeAccessLevel", value = API_INFO_MERGE_ACCESS_LEVEL, paramType = "query", required = true),
    })
    @Permission(permissionPublic = true)
    @PutMapping("/protected-branches")
    public ResponseEntity<ProtectedBranch> updateProtectedBranch(@PathVariable Long projectId,
                                                                 @PathVariable Long repositoryId,
                                                                 @RequestParam String branchName,
                                                                 @RequestParam Integer pushAccessLevel,
                                                                 @RequestParam Integer mergeAccessLevel) {
        return Results.success(gitlabBranchService.protectBranch(repositoryId, branchName, pushAccessLevel, mergeAccessLevel));
    }

    @ApiOperation(value = "删除保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "branchName", value = "分支名", paramType = "query", required = true),
    })
    @Permission(permissionPublic = true)
    @DeleteMapping("/protected-branches")
    public ResponseEntity<ProtectedBranch> updateProtectedBranch(@PathVariable Long projectId,
                                                                 @PathVariable Long repositoryId,
                                                                 @RequestParam String branchName) {
        gitlabBranchService.unprotectBranch(repositoryId, branchName);
        return Results.created(null);
    }
}
