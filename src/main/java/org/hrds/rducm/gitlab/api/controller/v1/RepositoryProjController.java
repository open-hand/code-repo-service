package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabUserViewDTO;
import org.hrds.rducm.gitlab.app.service.GitlabRepositoryService;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("repositoryProjController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories")
public class RepositoryProjController extends BaseController {
    @Autowired
    private GitlabRepositoryService gitlabRepositoryService;

    @ApiOperation(value = "查询所有已经启用的服务")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping("/list_by_active")
    public ResponseEntity<List<GitlabRepository>> listByActive(@PathVariable(value = "projectId") Long projectId) {
        return ResponseEntity.ok(gitlabRepositoryService.listByActive(projectId));
    }

}
