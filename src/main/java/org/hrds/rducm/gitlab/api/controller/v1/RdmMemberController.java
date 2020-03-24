package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ying.xie@hand-china.com
 */
//@Api(tags = SwaggerTags.RDM_MEMBER)
@RestController("rdmMemberController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab/repositories/{repositoryId}/members")
public class RdmMemberController extends BaseController {
    private final RdmMemberAppService rdmMemberAppService;

    @Autowired
    private IRdmMemberService rdmMemberService;

    public RdmMemberController(RdmMemberAppService rdmMemberAppService) {
        this.rdmMemberAppService = rdmMemberAppService;
    }

    @ApiOperation(value = "修改代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true),
            @ApiImplicitParam(name = "rdmMemberUpdateDTO", value = "参数", dataType = "RdmMemberUpdateDTO", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable Long organizationId,
                                          @PathVariable Long projectId,
                                          @PathVariable Long repositoryId,
                                          @PathVariable Long memberId,
                                          @RequestBody RdmMemberUpdateDTO rdmMemberUpdateDTO) {
        validObject(rdmMemberUpdateDTO);
        rdmMemberAppService.updateMember(memberId, rdmMemberUpdateDTO);
        return Results.success();
    }

    @ApiOperation(value = "移除代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true)
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long organizationId,
                                          @PathVariable Long projectId,
                                          @PathVariable Long repositoryId,
                                          @PathVariable Long memberId) {
        rdmMemberAppService.removeMember(memberId);
        return Results.success();
    }

    @ApiOperation(value = "手动同步代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true),
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{memberId}/sync")
    public ResponseEntity<?> syncMember(@PathVariable Long organizationId,
                                        @PathVariable Long projectId,
                                        @PathVariable Long repositoryId,
                                        @PathVariable Long memberId) {
        rdmMemberAppService.syncMember(memberId);
        return Results.success();
    }

    @ApiOperation(value = "同步Gitlab代码库成员到本服务")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/sync-from-gitlab")
    public ResponseEntity<?> syncMembersFromGitlab(@PathVariable Long organizationId,
                                                   @PathVariable Long projectId,
                                                   @PathVariable Long repositoryId) {
        rdmMemberService.syncAllMembersFromGitlab(organizationId, projectId, repositoryId);
        return Results.success();
    }
}
