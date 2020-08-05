package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TaskParam;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.asgard.schedule.enums.TriggerTypeEnum;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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
    @JobTask(maxRetryCount = 3,
            code = "membersAuditJob",
            description = "成员审计定时任务")
            //params = {@JobParam(name = "auditOrganizationId", description = "待审计组织id")})
    @TimedTask(name = "membersAuditJob",
            description = "成员审计定时任务",
            oneExecution = true,
            params = {@TaskParam(name= "auditOrganizationId", value = "7")},
            triggerType = TriggerTypeEnum.CRON_TRIGGER,
            cronExpression = "0 0 1 1 * ?")
    public void membersAuditJob(Map<String, Object> param) {
        // <> 获取组织
        long auditOrganizationId = Long.parseLong((String) param.get("auditOrganizationId"));
        logger.debug("参数组织id为[{}]", auditOrganizationId);

        logger.info("开始审计");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("组织" + auditOrganizationId);
        logger.info("开始审计组织[{}]的数据", auditOrganizationId);

        iMemberAuditService.auditMembersByOrganizationId(auditOrganizationId);

        stopWatch.stop();
        logger.info("审计组织[{}]的数据结束, 耗时[{}]ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());

        logger.info("结束审计, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }
}
