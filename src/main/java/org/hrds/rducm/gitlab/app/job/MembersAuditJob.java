package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 成员审计定时任务
 *
 * @author ying.xie@hand-china.com
 * @date 2020/5/25
 */
@Component
public class MembersAuditJob {
    private static final Logger logger = LoggerFactory.getLogger(MembersAuditJob.class);

    @Autowired
    private IMemberAuditService iMemberAuditService;

    /**
     * 成员审计定时任务
     */
    @Transactional(rollbackFor = Exception.class)
    @JobTask(maxRetryCount = 3,
            code = "membersAuditJob",
            description = "成员审计定时任务",
            params = {@JobParam(name = "organizationId", type = Long.class, description = "组织id")})
    public void membersAuditJob(Map<String, Object> map) {
        Object organizationId1 = map.get("organizationId");
        logger.debug(organizationId1.toString());

        List<Long> organizationIds = new ArrayList<>();

        // <0> 获取所有组织
        StopWatch stopWatch = new StopWatch();

        logger.info("开始审计");
        for (long i = 7; i < 8; i++) {
            stopWatch.start("组织" + i);

            Long organizationId = i;

            logger.info("开始审计组织[{}]的数据", organizationId);

            iMemberAuditService.auditMembersByOrganizationId(organizationId);

            stopWatch.stop();
            logger.info("审计组织[{}]的数据结束, 耗时[{}]ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());
        }
        logger.info("结束审计, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());

    }

}
