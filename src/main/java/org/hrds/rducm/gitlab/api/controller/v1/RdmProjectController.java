package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RepositoryPrivilegeViewDTO;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/7
 */
@RestController("rdmProjectController.v1")
@RequestMapping("/v1/organizations/{organizationId}/projects/{projectId}/gitlab")
public class RdmProjectController extends BaseController {
    @Autowired
    private IRdmMemberService iRdmMemberService;

    @ApiOperation(value = "查询成员所有应用服务的权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/members/{userId}/permissions")
    @ApiImplicitParams({

    })
    public ResponseEntity<Page<RdmMemberViewDTO>> pageMemberPermissions(@PathVariable Long organizationId,
                                                                        @PathVariable Long projectId,
                                                                        @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_COMMON) @PathVariable Long userId,
                                                                        PageRequest pageRequest) {
        return Results.success(iRdmMemberService.pageMemberPermissions(organizationId, projectId, userId, pageRequest));
    }

    /**
     * 获取用户拥有权限的代码库
     *
     * @param organizationId
     * @param projectId
     * @param userIds
     * @return
     */
    @ApiOperation(value = "获取用户拥有权限的代码库")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @PostMapping("/members/repositories/within")
    public ResponseEntity<List<RepositoryPrivilegeViewDTO>> listMemberRepositories(@PathVariable Long organizationId,
                                                                                   @PathVariable Long projectId,
                                                                                   @RequestBody Set<Long> userIds) {
        return Results.success(iRdmMemberService.selectRepositoriesByPrivilege(organizationId, projectId, userIds));
    }
}
