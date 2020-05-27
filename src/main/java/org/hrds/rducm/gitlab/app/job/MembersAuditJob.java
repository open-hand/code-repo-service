package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.annotation.TimedTask;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 成员审计定时任务 TODO
 *
 * @author ying.xie@hand-china.com
 * @date 2020/5/25
 */
@Component
public class MembersAuditJob {
    @Autowired
    private IMemberAuditService iMemberAuditService;

    /**
     * TODO 添加定时任务
     */
    private void audit() {
        List<Long> organizationIds = new ArrayList<>();

        organizationIds.parallelStream().forEach(orgId -> {
            iMemberAuditService.auditMembersByOrganizationId(orgId);
        });
    }

}
