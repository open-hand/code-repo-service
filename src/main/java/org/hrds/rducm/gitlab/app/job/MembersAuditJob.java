package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.annotation.JobTask;

import java.util.List;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

    /**
     * 平台内成员审计任务
     */
    @JobTask(maxRetryCount = 3,
            code = "membersAuditNewJob",
            description = "平台内代码库成员审计任务")
    public void membersAuditNewJob(Map<String, Object> map) {
        // <> 获取组织
        logger.info("开始审计");
        //查询所有的组织
        List<C7nTenantVO> c7nTenantVOS = c7nBaseServiceFacade.queryActiveOrganizations();
        if (CollectionUtils.isEmpty(c7nTenantVOS)){
            logger.info("平台内无组织");
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("membersAuditNewJob");
        c7nTenantVOS.forEach(c7nTenantVO -> {
            logger.info("开始审计组织[{}]的数据", c7nTenantVO.getTenantId());
            iMemberAuditService.auditMembersByOrganizationId(c7nTenantVO.getTenantId());
        });
        stopWatch.stop();
        logger.info("结束审计, 耗时[{}]s, \n{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }



}
