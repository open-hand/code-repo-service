package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.DetectApplicantTypeDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantPassDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApplicantCreateDTO;
import org.hrds.rducm.gitlab.api.controller.validator.RdmMemberApplicantValidator;
import org.hrds.rducm.gitlab.app.service.RdmMemberApplicantAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApplicantService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Set;

/**
 * 成员审批表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
@RestController("rdmMemberApprovalProjController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab/repositories/member-applicants")
public class RdmMemberApplicantProjController extends BaseController {

    @Autowired
    private IRdmMemberApplicantService iRdmMemberApplicantService;
    @Autowired
    private RdmMemberApplicantAppService rdmMemberApplicantAppService;
    @Autowired
    private RdmMemberApplicantValidator rdmMemberApplicantValidator;

    @ApiOperation(value = "成员权限申请列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "代码库id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "applicantUserName", value = "申请人(模糊)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "approvalState", value = "审批状态", paramType = "query", dataType = "String"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<RdmMemberApplicantViewDTO>> pageByOptions(@PathVariable Long projectId,
                                                                         @SortDefault(value = RdmMemberApplicant.FIELD_CREATION_DATE,
                                                                                 direction = Sort.Direction.DESC)
                                                                         @ApiIgnore PageRequest pageRequest,
                                                                         @RequestParam(required = false) Set<Long> repositoryIds,
                                                                         @RequestParam(required = false) String applicantUserName,
                                                                         @RequestParam(required = false) String approvalState) {

        return Results.success(iRdmMemberApplicantService.pageByOptions(projectId, pageRequest, repositoryIds, applicantUserName, approvalState));
    }

    @ApiOperation(value = "我的权限申请列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "size", paramType = "query", dataType = "Int"),
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "repositoryIds", value = "代码库id", paramType = "query", dataType = "Long", allowMultiple = true),
            @ApiImplicitParam(name = "approvalState", value = "审批状态", paramType = "query", dataType = "String"),
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/self")
    public ResponseEntity<Page<RdmMemberApplicantViewDTO>> pageByOptionsSelf(@PathVariable Long projectId,
                                                                             @SortDefault(value = RdmMemberApplicant.FIELD_CREATION_DATE,
                                                                                     direction = Sort.Direction.DESC)
                                                                             @ApiIgnore PageRequest pageRequest,
                                                                             @RequestParam(required = false) Set<Long> repositoryIds,
                                                                             @RequestParam(required = false) String approvalState) {

        return Results.success(iRdmMemberApplicantService.pageByOptionsSelf(projectId, pageRequest, repositoryIds, approvalState));
    }

    @ApiOperation(value = "检测当前用户申请类型")
    @Permission(level = ResourceLevel.ORGANIZATION)
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long organizationId,
                                    @PathVariable Long projectId,
                                    @RequestBody MemberApplicantCreateDTO memberApplicantCreateDTO) {
        validObject(memberApplicantCreateDTO);
        rdmMemberApplicantValidator.validateCreateDTO(projectId, memberApplicantCreateDTO);

        iRdmMemberApplicantService.createApproval(organizationId, projectId, memberApplicantCreateDTO);
        return Results.success();
    }

    @ApiOperation(value = "成员权限申请-审批通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", paramType = "path", required = true),
            @ApiImplicitParam(name = "objectVersionNumber", value = "版本号", paramType = "query", required = true),
            @ApiImplicitParam(name = "expiresAt", value = "过期时间", paramType = "query")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{id}/pass")
    public ResponseEntity<?> passAndHandleMember(@Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMA) @PathVariable Long id,
                                                 @RequestParam Long objectVersionNumber,
                                                 RdmMemberApplicantPassDTO passDTO) {
        validObject(passDTO);

        rdmMemberApplicantAppService.passAndHandleMember(id, objectVersionNumber, passDTO.getExpiresAt());
        return Results.success();
    }

    @ApiOperation(value = "成员权限申请-审批拒绝")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMA) @PathVariable Long id,
                                    @RequestParam Long objectVersionNumber,
                                    @RequestBody String approvalMessage) {
        rdmMemberApplicantAppService.refuse(id, objectVersionNumber, approvalMessage);
        return Results.success();
    }
}
