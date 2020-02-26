package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.gitlab4j.api.models.ProtectedTag;
import org.hrds.rducm.gitlab.app.service.GitlabTagService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabTagController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/tags")
public class GitlabTagController extends BaseController {
    @Autowired
    private GitlabTagService gitlabTagService;

    @ApiOperation(value = "查询保护标签")
    @Permission(permissionPublic = true)
    @GetMapping("/protected-tags")
    public ResponseEntity<List<ProtectedTag>> getProtectedTags(@PathVariable Long projectId,
                                                               @PathVariable Long repositoryId,
                                                               @RequestParam Integer glProjectId) {
        return Results.success(gitlabTagService.getProtectedTags(glProjectId));
    }

    @ApiOperation(value = "创建保护标签")
    @Permission(permissionPublic = true)
    @PostMapping("/protected-tags")
    public ResponseEntity<ProtectedTag> createProtectedTag(@PathVariable Long projectId,
                                                           @PathVariable Long repositoryId,
                                                           @RequestParam Integer glProjectId,
                                                           @RequestParam String glTagName,
                                                           @RequestParam Integer glCreateAccessLevel) {
        return Results.success(gitlabTagService.protectTag(glProjectId, glTagName, glCreateAccessLevel));
    }

    @ApiOperation(value = "修改保护标签")
    @Permission(permissionPublic = true)
    @PutMapping("/protected-tags")
    public ResponseEntity<ProtectedTag> updateProtectedTag(@PathVariable Long projectId,
                                                           @PathVariable Long repositoryId,
                                                           @RequestParam Integer glProjectId,
                                                           @RequestParam String glTagName,
                                                           @RequestParam Integer glCreateAccessLevel) {
        return Results.success(gitlabTagService.protectTag(glProjectId, glTagName, glCreateAccessLevel));
    }

    @ApiOperation(value = "删除保护标签")
    @Permission(permissionPublic = true)
    @DeleteMapping("/protected-tags")
    public ResponseEntity<ProtectedTag> deleteProtectedTag(@PathVariable Long projectId,
                                                           @PathVariable Long repositoryId,
                                                           @RequestParam Integer glProjectId,
                                                           @RequestParam String glTagName) {
        gitlabTagService.unprotectTag(glProjectId, glTagName);
        return Results.success();
    }
}
