package org.hrds.rducm.gitlab.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApprovalCreateDTO;
import org.hrds.rducm.gitlab.api.controller.validator.RdmMemberApprovalValidator;
import org.hrds.rducm.gitlab.app.service.RdmMemberApprovalAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApprovalService;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 成员审批表 管理 API
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
@RestController("rdmMemberApprovalProjController.v1")
@RequestMapping("/v1/projects/{projectId}/gitlab/repositories/member-approvals")
public class RdmMemberApprovalProjController extends BaseController {

    @Autowired
    private IRdmMemberApprovalService iRdmMemberApprovalService;
    @Autowired
    private RdmMemberApprovalAppService rdmMemberApprovalAppService;
    @Autowired
    private RdmMemberApprovalValidator rdmMemberApprovalValidator;

    @ApiOperation(value = "成员审批表列表")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @GetMapping
    public ResponseEntity<PageInfo<RdmMemberApproval>> list(@PathVariable Long projectId,
                                                            PageRequest pageRequest) {

        return Results.success(iRdmMemberApprovalService.pageByOptions(projectId, pageRequest));
    }


    @ApiOperation(value = "创建成员权限申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = ApiInfoConstants.PROJECT_ID, paramType = "path", required = true),
            @ApiImplicitParam(name = "memberApprovalCreateDTO", value = "参数", paramType = "body", dataType = "MemberApprovalCreateDTO", required = true)
    })
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long projectId,
                                    @RequestBody MemberApprovalCreateDTO memberApprovalCreateDTO) {
        validObject(memberApprovalCreateDTO);
        rdmMemberApprovalValidator.validateCreateDTO(projectId, memberApprovalCreateDTO);

        iRdmMemberApprovalService.createApproval(projectId, memberApprovalCreateDTO);
        return Results.success();
    }

    @ApiOperation(value = "成员权限申请-审批通过")
    @Permission(type = ResourceType.PROJECT, permissionPublic = true)
    @PostMapping("/{id}/pass")
    public ResponseEntity<?> passAndHandleMember(@PathVariable Long id,
                                                 @RequestParam Long objectVersionNumber,
                                                 @RequestParam(required = false) Date expiresAt) {
        rdmMemberApprovalValidator.validatePass(id);

        rdmMemberApprovalAppService.passAndHandleMember(id, objectVersionNumber, expiresAt);
        return Results.success();
    }

    @ApiOperation(value = "成员权限申请-审批拒绝")
    @Permission()
    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@PathVariable Long id,
                                    @RequestParam Long objectVersionNumber) {
        rdmMemberApprovalValidator.validateRefuse(id);

        rdmMemberApprovalAppService.refuse(id, objectVersionNumber);
        return Results.success();
    }
}
