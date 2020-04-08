package org.hrds.rducm.gitlab.domain.component;

import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

/**
 * 模糊条件查询的工具类
 *
 * @author ying.xie@hand-china.com
 * @date 2020/4/8
 */
@Component
public class QueryConditionHelper {
    private static IC7nBaseServiceService ic7nBaseServiceService;
    private static IC7nDevOpsServiceService ic7nDevOpsServiceService;

    /**
     * 项目层
     * 按realName和loginName查询
     *
     * @param projectId
     * @param realName
     * @param loginName
     * @return 返回一个用户id的集合
     * 1. 集合为null, 表示跳过该查询
     * 2. 集合不为null
     * 2.1 集合size=0, 表示未查找到数据
     * 2.2 集合size>0, 表示查找到数据
     */
    public static Set<Long> queryByNameConditionOnProj(Long projectId, String realName, String loginName) {
        // 调用外部接口模糊查询 用户名或登录名
        if (!StringUtils.isEmpty(realName) || !StringUtils.isEmpty(loginName)) {
            Set<Long> userIdsSet = ic7nBaseServiceService.listC7nUserIdsByNameOnProjectLevel(projectId, realName, loginName);

            return userIdsSet.isEmpty() ? Collections.emptySet() : userIdsSet;
        }

        return null;
    }

    @Autowired
    public void setIc7nBaseServiceService(IC7nBaseServiceService ic7nBaseServiceService) {
        QueryConditionHelper.ic7nBaseServiceService = ic7nBaseServiceService;
    }

    @Autowired
    public static void setIc7nDevOpsServiceService(IC7nDevOpsServiceService ic7nDevOpsServiceService) {
        QueryConditionHelper.ic7nDevOpsServiceService = ic7nDevOpsServiceService;
    }
}
