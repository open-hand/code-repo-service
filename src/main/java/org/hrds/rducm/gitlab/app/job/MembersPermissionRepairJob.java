package org.hrds.rducm.gitlab.app.job;

import java.util.Map;
import java.util.Objects;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TaskParam;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.asgard.schedule.enums.TriggerTypeEnum;
import org.hrds.rducm.gitlab.domain.service.IMemberPermissionRepairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 成员权限修复定时任务
 *
 * @author mofei.li@hand-china.com 2020/10/26 16:07
 */
@Component
public class MembersPermissionRepairJob {
    private static final Logger logger = LoggerFactory.getLogger(MembersPermissionRepairJob.class);

    @Autowired
    private IMemberPermissionRepairService iMemberPermissionRepairService;

    /**
     * 成员权限修复任务
     */
    @JobTask(maxRetryCount = 3,
            code = "membersPermissionRepairJob",
            description = "代码库成员权限修复任务",
            params = {@JobParam(name = "repairOrganizationId", description = "待修复组织id")})
    public void membersPermissionRepairJob(Map<String, Object> param){
        // <> 获取组织
        long repairOrganizationId = 0L;
        if (param.containsKey("repairOrganizationId") && Objects.nonNull(param.get("repairOrganizationId"))) {
            repairOrganizationId = Long.parseLong(param.get("repairOrganizationId").toString());
        }
        logger.debug("参数组织id为[{}]", repairOrganizationId);

        logger.info("开始修复");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("组织" + repairOrganizationId);
        logger.info("开始修复组织[{}]的数据", repairOrganizationId);

        iMemberPermissionRepairService.repairMemberPermission(repairOrganizationId);

        stopWatch.stop();
        logger.info("修复组织[{}]的数据结束, 耗时[{}]ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());

        logger.info("结束修复, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }

//    @TimedTask(name = "membersPermissionRepairTimeTask",
//            description = "代码库成员权限修复定时任务",
//            params = {@TaskParam(name = "repairOrganizationId", value = "1009")},
//            triggerType = TriggerTypeEnum.CRON_TRIGGER,
//            cronExpression = "0 0 3 * * ?")
    public void membersPermissionRepairTimeTask(Map<String, Object> param){
        // <> 获取组织
        long repairOrganizationId = 0L;
        if (param.containsKey("repairOrganizationId") && Objects.nonNull(param.get("repairOrganizationId"))) {
            repairOrganizationId = Long.parseLong(param.get("repairOrganizationId").toString());
        }
        logger.debug("参数组织id为[{}]", repairOrganizationId);

        logger.info("开始修复");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("组织" + repairOrganizationId);
        logger.info("开始修复组织[{}]的数据", repairOrganizationId);

        iMemberPermissionRepairService.repairMemberPermission(repairOrganizationId);

        stopWatch.stop();
        logger.info("修复组织[{}]的数据结束, 耗时[{}]ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());

        logger.info("结束修复, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }
}
