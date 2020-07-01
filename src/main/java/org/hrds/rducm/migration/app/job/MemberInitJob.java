package org.hrds.rducm.migration.app.job;

import io.choerodon.asgard.schedule.annotation.JobTask;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberService;
import org.hrds.rducm.migration.domain.service.Version023STemp1ervice;
import org.hrds.rducm.migration.domain.service.Version023Service;
import org.hrds.rducm.migration.domain.service.Version023TempFixService;
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
    private Version023Service version023Service;

    @Autowired
    private Version023TempFixService version023TempFixService;

    @Autowired
    private Version023STemp1ervice version023STemp1ervice;

    /**
     * 0.23.0版本上线时初始化Gitlab成员到代码库
     *
     * @param map
     */
    @JobTask(maxRetryCount = 3,
            code = "initRdmMembers",
            description = "0.23版本代码库初始化成员")
    public void initRdmMembers(Map<String, Object> map) {
        logger.info("开始初始化");

        version023Service.initAllPrivilegeOnSiteLevel();

        logger.info("Gitlab成员初始化完成");
    }

    /**
     * 临时修复023初始化代码库遗漏的数据
     *
     * @param map
     */
    @JobTask(maxRetryCount = 3,
            code = "fixInitRdmMembers",
            description = "修复0.23版本代码库初始化成员遗漏数据")
    public void fixInitRdmMembers(Map<String, Object> map) {
        logger.info("开始初始化");

        version023TempFixService.initAllPrivilegeOnSiteLevel();

        logger.info("Gitlab成员初始化完成");
    }


    /**
     * 临时修复023初始化代码库遗漏的数据(优化版)
     *
     * @param map
     */
    @JobTask(maxRetryCount = 3,
            code = "fixInitRdmMembersPlus",
            description = "(优化版)修复0.23版本代码库初始化成员遗漏数据")
    public void fixInitRdmMembersPlus(Map<String, Object> map) {
        logger.info("开始初始化");

        version023STemp1ervice.initAllPrivilegeOnSiteLevel();

        logger.info("Gitlab成员初始化完成");
    }
}
