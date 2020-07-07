package org.hrds.rducm.migration.app.job;

import io.choerodon.asgard.schedule.annotation.JobTask;
import org.hrds.rducm.migration.domain.service.Version023Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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
}
