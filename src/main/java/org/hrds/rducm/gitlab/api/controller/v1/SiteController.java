package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.config.SwaggerTags;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("siteController.v1")
@RequestMapping("/v1/gitlab")
public class SiteController extends BaseController {
    @Autowired
    private GitlabUserRepository gitlabUserRepository;

    @ApiOperation(value = "查询gitlab用户(平台层)")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping("/users")
    public ResponseEntity<Object> queryUser(@RequestParam String username) {
        return Results.success(gitlabUserRepository.getUserFromGitlab(username));
    }
}
