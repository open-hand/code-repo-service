package org.hrds.rducm.gitlab.app.job;

import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员审计定时任务 TODO
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
     * TODO 添加定时任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit() {
        List<Long> organizationIds = new ArrayList<>();

        // <0> 获取所有组织
        StopWatch stopWatch = new StopWatch();

        logger.info("开始审计");
        for (long i = 0; i < 100; i++) {
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
