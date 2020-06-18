package org.hrds.rducm.migration.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.migration.domain.service.Version023Service;
import org.hzero.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/12
 */
@RestController
@RequestMapping("/v1/migration/test")
public class MigTestController extends BaseController {
    @Autowired
    private Version023Service version023Service;

    /**
     * TODO delete
     * @param organizationId
     * @return
     */
    @ApiOperation(value = "查询")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @PostMapping("/orgLevel")
    public ResponseEntity<List<C7nUserVO>> orgLevel(Long organizationId) {
//        Long userId = DetailsHelper.getUserDetails().getUserId();

        version023Service.initAllPrivilegeOnSiteLevel();

        return null;
    }
}
