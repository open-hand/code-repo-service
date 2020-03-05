package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时Controller, 里面的接口需删除
 * 用于存放定时任务等的测试接口
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
@RestController
@RequestMapping("/v1/gitlab/temp")
public class TempController extends BaseController {

    @Autowired
    private GitlabMemberService gitlabMemberService;

    @ApiOperation(value = "处理过期成员")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @GetMapping("/handle-expired-members")
    public ResponseEntity<?> handleExpiredMembers() {
        gitlabMemberService.handleExpiredMembers();
        return Results.success();
    }
}
