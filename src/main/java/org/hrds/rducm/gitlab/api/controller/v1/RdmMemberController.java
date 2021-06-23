package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable Long organizationId,
                                          @PathVariable Long projectId,
                                          @Encrypt @PathVariable Long repositoryId,
                                          @Encrypt @PathVariable Long memberId,
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long organizationId,
                                          @PathVariable Long projectId,
                                          @Encrypt @PathVariable Long repositoryId,
                                          @Encrypt @PathVariable Long memberId) {
        rdmMemberAppService.removeMember(memberId);
        return Results.success();
    }

    @ApiOperation(value = "手动同步代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberId", value = "成员id", paramType = "path", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{memberId}/sync")
    public ResponseEntity<?> syncMember(@PathVariable Long organizationId,
                                        @PathVariable Long projectId,
                                        @Encrypt @PathVariable Long repositoryId,
                                        @Encrypt @PathVariable Long memberId) {
        rdmMemberAppService.syncMember(memberId);
        return Results.success();
    }

    @ApiOperation(value = "手动批量同步代码库成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryId", value = ApiInfoConstants.REPOSITORY_ID, paramType = "path", required = true),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch/sync")
    public ResponseEntity<?> syncBatchMember(@PathVariable Long organizationId,
                                             @PathVariable Long projectId,
                                             @Encrypt @PathVariable Long repositoryId,
                                             @Encrypt @RequestBody List<Long> memberIds) {
        rdmMemberAppService.syncBatchMember(memberIds);
        return Results.success();
    }

}
