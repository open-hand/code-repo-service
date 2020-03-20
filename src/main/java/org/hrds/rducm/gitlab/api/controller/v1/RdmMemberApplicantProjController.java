package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.DetectApplicantTypeDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApplicantCreateDTO;
import org.hrds.rducm.gitlab.api.controller.validator.RdmMemberApprovalValidator;
import org.hrds.rducm.gitlab.app.service.RdmMemberApplicantAppService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApplicantService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Future;
import java.util.Date;

/**
 * 成员审批表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
@RestController("rdmMemberApprovalProjController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/member-applicants")
public class RdmMemberApplicantProjController extends BaseController {

    @Autowired
    private IRdmMemberApplicantService iRdmMemberApplicantService;
    @Autowired
    private RdmMemberApplicantAppService rdmMemberApplicantAppService;
    @Autowired
    private RdmMemberApprovalValidator rdmMemberApprovalValidator;

    @ApiOperation(value = "成员权限申请列表")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberApplicantViewDTO>> list(@PathVariable Long projectId,
                                                                    PageRequest pageRequest) {

        return Results.success(iRdmMemberApplicantService.pageByOptions(projectId, pageRequest));
    }

    @ApiOperation(value = "检测当前用户申请类型")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/self/detect-applicant-type")
    public ResponseEntity<DetectApplicantTypeDTO> detectApplicantType(@PathVariable Long projectId,
                                                                      @RequestParam Long repositoryId) {
        DetectApplicantTypeDTO dto = iRdmMemberApplicantService.detectApplicantType(projectId, repositoryId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "创建成员权限申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberApplicantCreateDTO", value = "参数", paramType = "body", dataType = "MemberApplicantCreateDTO", required = true)
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long projectId,
                                    @RequestBody MemberApplicantCreateDTO memberApplicantCreateDTO) {
        validObject(memberApplicantCreateDTO);
        rdmMemberApprovalValidator.validateCreateDTO(projectId, memberApplicantCreateDTO);

        iRdmMemberApplicantService.createApproval(projectId, memberApplicantCreateDTO);
        return Results.success();
    }

    @ApiOperation(value = "成员权限申请-审批通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", paramType = "path", required = true),
            @ApiImplicitParam(name = "objectVersionNumber", value = "版本号", paramType = "query", required = true),
            @ApiImplicitParam(name = "expiresAt", value = "过期时间", paramType = "query")
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{id}/pass")
    public ResponseEntity<?> passAndHandleMember(@PathVariable Long id,
                                                 @RequestParam Long objectVersionNumber,
                                                 @Future @RequestParam(required = false) Date expiresAt) {
        if (expiresAt != null) {
            validObject(expiresAt);
        }
        rdmMemberApprovalValidator.validatePass(id);

        rdmMemberApplicantAppService.passAndHandleMember(id, objectVersionNumber, expiresAt);
        return Results.success();
    }

    @ApiOperation(value = "成员权限申请-审批拒绝")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@PathVariable Long id,
                                    @RequestParam Long objectVersionNumber,
                                    @RequestBody String approvalMessage) {
        rdmMemberApprovalValidator.validateRefuse(id);

        rdmMemberApplicantAppService.refuse(id, objectVersionNumber, approvalMessage);
        return Results.success();
    }
}
