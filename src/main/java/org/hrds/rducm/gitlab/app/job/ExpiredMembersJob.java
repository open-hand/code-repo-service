package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 处理权限到期成员的定时任务
 *
 * @author ying.xie@hand-china.com
 * @date 2020/5/25
 */
@Component
public class ExpiredMembersJob {
    private static final Logger logger = LoggerFactory.getLogger(ExpiredMembersJob.class);

    @Autowired
    private RdmMemberAppService rdmMemberAppService;

    @JobTask(maxRetryCount = 3, code = "handleExpiredMembers", description = "代码库移除过期成员")
    @TimedTask(name = "代码库移除过期成员",
            description = "代码库移除过期成员",
            oneExecution = true,
            repeatCount = 3,
            repeatInterval = 5,
            repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.MINUTES,
            params = {})
    public void handleExpiredMembers(Map<String, Object> map) {
        // 执行方法
        logger.info("移除过期成员定时任务开始执行");

        rdmMemberAppService.handleExpiredMembers();

        logger.info("移除过期成员定时任务执行完毕");
    }
}
