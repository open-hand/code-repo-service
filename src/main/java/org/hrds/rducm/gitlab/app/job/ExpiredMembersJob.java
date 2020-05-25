package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
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
    @Autowired
    private RdmMemberAppService rdmMemberAppService;

    @TimedTask(name = "代码库移除过期成员",
            description = "代码库移除过期成员",
            oneExecution = true,
            repeatCount = 0,
            repeatInterval = 100,
            repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
            params = {})
    public void handleExpiredMembers(Map<String, Object> map) {
        // 执行方法
        rdmMemberAppService.handleExpiredMembers();
    }
}
