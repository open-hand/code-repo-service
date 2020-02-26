package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabMemberDTO;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<Page<GitlabMemberDTO>> pageByOptions(@PathVariable Long projectId,
                                                               PageRequest pageRequest,
                                                               GitlabMemberDTO query) {
        return Results.success(gitlabMemberService.list(projectId, pageRequest));
    }

    @ApiOperation(value = "批量新增代码库成员(项目层)")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/members/batch-add")
    public ResponseEntity<Object> batchAddMembers(@PathVariable Long projectId,
                                                  @RequestBody @Valid List<GitlabMemberDTO> gitlabMembers) {
        gitlabMemberService.batchAddMembers(projectId, gitlabMembers);
        return Results.created(null);
    }
}
