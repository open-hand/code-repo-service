package org.hrds.rducm.gitlab.api.controller.v1;

import org.hrds.rducm.gitlab.app.eventhandler.RdmMemberChangeSagaHandler;
import org.hrds.rducm.gitlab.app.job.MembersAuditJob;
import org.hrds.rducm.gitlab.app.job.MembersPermissionRepairJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/10/22
 */
@RestController("rdmUserUserController.v2")
@RequestMapping("/v1/test")
public class TestController {

    @Autowired
    private RdmMemberChangeSagaHandler rdmMemberChangeSagaHandler;

    @Autowired
    private MembersPermissionRepairJob membersPermissionRepairJob;

    @Autowired
    private MembersAuditJob membersAuditJob;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/test")
    public void test() {

        String msg="{\n" +
                "  \"organizationId\": 1486,\n" +
                "  \"projectId\": 243688320436453376,\n" +
                "  \"recordIds\": [357233, 357234]\n" +
                "}";
//        String msg = "{ \"organizationId\": 1426, \"projectId\": 228837150103220224, \"recordIds\": null }\n";
//        rdmMemberChangeSagaHandler.projectAudit(msg);
//        membersPermissionRepairJob.membersPermissionRepairNewJob(null);
        membersAuditJob.membersAuditNewJob(null);
    }
}
