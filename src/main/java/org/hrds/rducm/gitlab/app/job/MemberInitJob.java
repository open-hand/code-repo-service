package org.hrds.rducm.gitlab.app.job;

import org.hrds.rducm.gitlab.domain.facade.IC7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.IC7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.Set;

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
    private IC7nBaseServiceFacade ic7NBaseServiceFacade;
    @Autowired
    private IC7nDevOpsServiceFacade ic7NDevOpsServiceFacade;

    /**
     * 上线时初始化Gitlab成员到代码库 TODO
     *
     * @param map
     */
//    @JobTask(maxRetryCount = 3,
//            code = "initRdmMembers",
//            description = "代码库移除过期成员")
    @Transactional(rollbackFor = Exception.class)
    public void initRdmMembers(Map<String, Object> map) {
//        Long organizationId = (Long) map.get("organizationId");

        StopWatch stopWatch = new StopWatch();

        // <0> 获取所有组织
        for (long i = 0; i < 10; i++) {
            stopWatch.start("组织" + i);

            Long organizationId = i;

            // <1> 获取组织下所有项目
            Set<Long> projectIds = ic7NBaseServiceFacade.listProjectIds(organizationId);

            logger.info("该组织{} 下的所有项目为{}", organizationId, projectIds);

            // <2> 获取项目下所有代码库id和Gitlab项目id
            projectIds.forEach(projectId -> {
                Map<Long, Long> appServiceIdMap = ic7NDevOpsServiceFacade.listC7nAppServiceIdsMapOnProjectLevel(projectId);

                appServiceIdMap.forEach((repositoryId, glProjectId) -> {
                    logger.info("组织id为{}, 项目id为{}, 代码库id为{}", organizationId, projectId, repositoryId);
                    int count = iRdmMemberService.syncAllMembersFromGitlab(organizationId, projectId, repositoryId);
                    logger.info("此次导入了{}个成员", count);
                });
            });

            stopWatch.stop();
        }

        logger.info("Gitlab成员初始化完成, \n{}", stopWatch.prettyPrint());
    }
}
