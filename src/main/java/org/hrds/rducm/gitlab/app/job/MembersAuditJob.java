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
import java.util.Objects;

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
     * 成员审计任务
     */
    @JobTask(maxRetryCount = 3,
            code = "membersAuditJob",
            description = "代码库成员审计任务",
            params = {@JobParam(name = "auditOrganizationId", description = "待审计组织id")})
    public void membersAuditJob(Map<String, Object> param) {
        // <> 获取组织
        long auditOrganizationId = 0L;
        if (param.containsKey("auditOrganizationId") && Objects.nonNull(param.get("auditOrganizationId"))) {
            auditOrganizationId = Long.parseLong(param.get("auditOrganizationId").toString());
        }
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


    /**
     * 成员审计任务
     */
    @TimedTask(name = "membersAuditTimeTask",
            description = "代码库成员审计定时任务",
            params = {@TaskParam(name = "auditOrganizationId", value = "1009")},
            triggerType = TriggerTypeEnum.CRON_TRIGGER,
            cronExpression = "0 0 2 * * ?")
    public void membersAuditTimeTask(Map<String, Object> param) {
        // <> 获取组织
        long auditOrganizationId = 0L;
        if (param.containsKey("auditOrganizationId") && Objects.nonNull(param.get("auditOrganizationId"))) {
            auditOrganizationId = Long.parseLong(param.get("auditOrganizationId").toString());
        }

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
