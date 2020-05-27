package org.hrds.rducm.gitlab.app.job;

import com.google.common.base.Stopwatch;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 初始化Gitlab权限到代码库的任务
 *
 * @author ying.xie@hand-china.com
 * @date 2020/5/26
 */
@Component
public class MemberInitJob {
    private static final Logger logger = LoggerFactory.getLogger(MemberInitJob.class);

    @Autowired
    private IRdmMemberService iRdmMemberService;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;

    /**
     * 上线时初始化Gitlab成员到代码库
     *
     * @param map
     */
    @JobTask(maxRetryCount = 3,
            code = "initRdmMembers",
            description = "代码库移除过期成员")
    @Transactional(rollbackFor = Exception.class)
    public void initRdmMembers(Map<String, Object> map) {
//        Long organizationId = (Long) map.get("organizationId");

        // <0> 获取所有组织
        for (long i = 0; i < 730; i++) {
            Long organizationId = i;

            // <1> 获取组织下所有项目
            Set<Long> projectIds = ic7nBaseServiceService.listProjectIds(organizationId);

            logger.info("该组织{} 下的所有项目为{}", organizationId, projectIds);

            // <2> 获取项目下所有代码库id和Gitlab项目id
            projectIds.forEach(projectId -> {
                Map<Long, Long> appServiceIdMap = ic7nDevOpsServiceService.listC7nAppServiceIdsMapOnProjectLevel(projectId);

                appServiceIdMap.forEach((repositoryId, glProjectId) -> {
                    logger.info("组织id为{}, 项目id为{}, 代码库id为{}", organizationId, projectId, repositoryId);
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    int count = iRdmMemberService.syncAllMembersFromGitlab(organizationId, projectId, repositoryId);
                    logger.info("此次导入了{}个成员", count);

                    long elapsed = stopwatch.elapsed(TimeUnit.MICROSECONDS);
                    logger.info("耗时 {} ms", elapsed);
                });
            });
        }
    }
}
