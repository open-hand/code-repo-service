package org.hrds.rducm.gitlab.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.api.controller.dto.RepositoryPrivilegeViewDTO;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author 14589 2020/12/09 13:49
 */
@RestController("rdmOrgController.v1")
@RequestMapping("/v1/organizations/{organizationId}/gitlab")
public class RdmOrgController {

    @Autowired
    private IRdmMemberService iRdmMemberService;

    @ApiOperation(value = "组织层-查询用户拥有Developer权限的应用服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/developer/{userId}/repositories/within")
    public ResponseEntity<RepositoryPrivilegeViewDTO> listMemberPermissions(@PathVariable Long organizationId,
                                                                            @Encrypt @PathVariable Long userId) {
        return Results.success(iRdmMemberService.selectOrgRepositoriesByDeveloper(organizationId, userId));
    }

}
