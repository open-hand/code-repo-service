package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.GitlabUserViewDTO;
import org.hrds.rducm.gitlab.app.service.GitlabUserService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API接口
 */
//@Api(tags = SwaggerTags.GITLAB_USER)
@RestController("gitlabUserController.v1")
@RequestMapping("/v1/gitlab/users")
public class GitlabUserController extends BaseController {
    @Autowired
    private GitlabUserService gitlabUserService;

    @ApiOperation(value = "查询个人信息")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping("/self")
    public ResponseEntity<GitlabUserViewDTO> queryUser() {
        return Results.success(gitlabUserService.queryUserSelf());
    }

    @ApiOperation(value = "新建用户")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping("/{userId}")
    public ResponseEntity<Object> createUser(@PathVariable Long userId,
                                             @RequestParam String glEmail,
                                             @RequestParam String glUsername,
                                             @RequestParam String glName) {
        gitlabUserService.createUserWithRandomPassword(userId, glEmail, glUsername, glName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @ApiOperation(value = "修改个人密码")
//    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
//    })
//    @PutMapping("/personal/password")
//    public ResponseEntity<Object> updateUserPassword(@RequestParam String password,
//                                                     @RequestParam String confirmPassword) {
//        gitlabUserService.updatePasswordForUser(password, confirmPassword);
//        return Results.created(null);
//    }
}