package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.ProtectedBranchDTO;
import org.hrds.rducm.gitlab.app.service.RdmBranchAppService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.RDM_BRANCH)
@RestController("rdmBranchController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/branches")
public class RdmBranchController extends BaseController {
    public static final String API_INFO_PUSH_ACCESS_LEVEL = "是否允许推送-权限级别(0|30|40)";
    public static final String API_INFO_MERGE_ACCESS_LEVEL = "是否允许合并-权限级别(0|30|40)";

    @Autowired
    private RdmBranchAppService rdmBranchAppService;

    @ApiOperation(value = "查询分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "excludeProtectedFlag", value = "是否排除保护分支", paramType = "query", dataType = "boolean"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<BranchDTO>> getBranches(@PathVariable Long projectId,
                                                       @PathVariable Long repositoryId,
                                                       BranchQueryDTO branchQueryDTO) {
        return Results.success(rdmBranchAppService.getBranches(projectId, repositoryId, branchQueryDTO));
    }

    @ApiOperation(value = "查询保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/protected-branches")
    public ResponseEntity<List<ProtectedBranchDTO>> getProtectedBranches(@PathVariable Long projectId,
                                                                         @PathVariable Long repositoryId) {
        return Results.success(rdmBranchAppService.getProtectedBranches(projectId, repositoryId));
    }

    @ApiOperation(value = "添加保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "branchName", value = "分支名(可使用通配符)", paramType = "query", required = true),
            @ApiImplicitParam(name = "pushAccessLevel", value = API_INFO_PUSH_ACCESS_LEVEL, paramType = "query", required = true),
            @ApiImplicitParam(name = "mergeAccessLevel", value = API_INFO_MERGE_ACCESS_LEVEL, paramType = "query", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/protected-branches")
    public ResponseEntity<ProtectedBranchDTO> createProtectedBranch(@PathVariable Long projectId,
                                                                    @PathVariable Long repositoryId,
                                                                    @RequestParam String branchName,
                                                                    @RequestParam Integer pushAccessLevel,
                                                                    @RequestParam Integer mergeAccessLevel) {
        return Results.created(rdmBranchAppService.protectBranch(projectId, repositoryId, branchName, pushAccessLevel, mergeAccessLevel));
    }

    @ApiOperation(value = "修改保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "branchName", value = "分支名", paramType = "query", required = true),
            @ApiImplicitParam(name = "pushAccessLevel", value = API_INFO_PUSH_ACCESS_LEVEL, paramType = "query", required = true),
            @ApiImplicitParam(name = "mergeAccessLevel", value = API_INFO_MERGE_ACCESS_LEVEL, paramType = "query", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/protected-branches")
    public ResponseEntity<ProtectedBranchDTO> updateProtectedBranch(@PathVariable Long projectId,
                                                                    @PathVariable Long repositoryId,
                                                                    @RequestParam String branchName,
                                                                    @RequestParam Integer pushAccessLevel,
                                                                    @RequestParam Integer mergeAccessLevel) {
        return Results.created(rdmBranchAppService.updateProtectedBranch(projectId, repositoryId, branchName, pushAccessLevel, mergeAccessLevel));
    }

    @ApiOperation(value = "删除保护分支")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "branchName", value = "分支名", paramType = "query", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/protected-branches")
    public ResponseEntity<?> removeProtectedBranch(@PathVariable Long projectId,
                                                   @PathVariable Long repositoryId,
                                                   @RequestParam String branchName) {
        rdmBranchAppService.unprotectBranch(projectId, repositoryId, branchName);
        return Results.success();
    }
}
