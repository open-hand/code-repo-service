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
 * Created by wangxiang on 2022/1/17
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

        String msg = "{ \"organizationId\": 1568, \"projectId\": 266864222088351744, \"recordIds\": null }\n";
//        String msg = "{ \"organizationId\": 1426, \"projectId\": 228837150103220224, \"recordIds\": null }\n";
        rdmMemberChangeSagaHandler.projectAudit(msg);
//        membersPermissionRepairJob.membersPermissionRepairNewJob(null);
        String msg1 = "[{\n" +
                "\t\"objectVersionNumber\": 1,\n" +
                "\t\"_status\": \"create\",\n" +
                "\t\"id\": 238596563373568000,\n" +
                "\t\"organizationId\": 33,\n" +
                "\t\"projectId\": 778,\n" +
                "\t\"repositoryId\": 223125748620181504,\n" +
                "\t\"userId\": 22548,\n" +
                "\t\"glProjectId\": 24196,\n" +
                "\t\"glUserId\": 21879,\n" +
                "\t\"glAccessLevel\": 30\n" +
                "}]";
//        rdmMemberChangeSagaHandler.batchAddOrUpdateMembers(msg1);
//        membersAuditJob.membersAuditNewJob(null);
    }
}
