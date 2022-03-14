package org.hrds.rducm.gitlab.app.job;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TaskParam;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.asgard.schedule.enums.TriggerTypeEnum;

import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.service.IMemberPermissionRepairService;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;


    @JobTask(maxRetryCount = 3,
            code = "membersPermissionRepairNewJob",
            description = "平台内代码库成员权限修复任务")
    public void membersPermissionRepairNewJob(Map<String, Object> param) {
        // <> 获取组织
        List<C7nTenantVO> c7nTenantVOS = c7nBaseServiceFacade.listAllOrgs();
        if (CollectionUtils.isEmpty(c7nTenantVOS)) {
            logger.info("平台内无组织");
            return;
        }
        logger.info("开始修复");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("membersPermissionRepairNewJob");
        //JobTask 封装了事务，修复每一个组织的数据启用嵌套事务，子事务不影响父级事务，父级事务影响子集事务，子事务之间相互独立
        c7nTenantVOS.forEach(c7nTenantVO -> {
            iMemberPermissionRepairService.repairMemberPermission(c7nTenantVO.getTenantId());
        });
        stopWatch.stop();
        logger.info("结束修复, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }


    @JobTask(maxRetryCount = 3,
            code = "membersBatchSyncJob",
            description = "平台内代码库成员批量同步任务")
    public void membersBatchSyncJob(Map<String, Object> param) {
        // <> 获取组织
        List<C7nTenantVO> c7nTenantVOS = c7nBaseServiceFacade.listAllOrgs();
        if (CollectionUtils.isEmpty(c7nTenantVOS)) {
            logger.info("平台内无组织");
            return;
        }
        logger.info("开始修复");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("membersPermissionRepairNewJob");
        c7nTenantVOS.forEach(c7nTenantVO -> {
            iMemberPermissionRepairService.membersBatchSyncJob(c7nTenantVO.getTenantId());
        });
        stopWatch.stop();
        logger.info("结束修复, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }


}
