package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ying.xie@hand-china.com
 */
//@Api(tags = SwaggerTags.GITLAB_MEMBER)
@RestController("gitlabMemberController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/{repositoryId}/members")
public class RdmMemberController extends BaseController {
    private final RdmMemberService rdmMemberService;

    public RdmMemberController(RdmMemberService rdmMemberService) {
        this.rdmMemberService = rdmMemberService;
    }

    @ApiOperation(value = "修改代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true),
            @ApiImplicitParam(name = "rdmMemberUpdateDTO", value = "参数", dataType = "RdmMemberUpdateDTO", required = true),
    })
    @Permission(permissionPublic = true)
    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable Long projectId,
                                               @PathVariable Long repositoryId,
                                               @PathVariable Long memberId,
                                               @RequestBody RdmMemberUpdateDTO rdmMemberUpdateDTO) {
        validObject(rdmMemberUpdateDTO);
        rdmMemberService.updateMember(memberId, rdmMemberUpdateDTO);
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
    public ResponseEntity<?> removeMember(@PathVariable Long projectId,
                                          @PathVariable Long repositoryId,
                                          @PathVariable Long memberId) {
        rdmMemberService.removeMember(memberId);
        return Results.created(null);
    }
}
