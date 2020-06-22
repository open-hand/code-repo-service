package org.hrds.rducm.gitlab.app.async;

import io.choerodon.core.domain.Page;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hzero.mybatis.domian.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * TODO 查询多线程优化, 待完成
 * @author ying.xie@hand-china.com
 * @date 2020/6/8
 */
@Component
public class RdmMemberQueryHelper {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberQueryHelper.class);
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;

    @Async
    public ListenableFuture<Integer> userCondition(Long projectId, String realName, String loginName, Condition condition) {
        logger.info("封装用户查询, 异步开启");
        // 调用外部接口模糊查询 用户名或登录名
        if (!StringUtils.isEmpty(realName) || !StringUtils.isEmpty(loginName)) {
            Set<Long> userIdsSet = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, realName, loginName);

            if (userIdsSet.isEmpty()) {
                return new AsyncResult<>(0);
            } else {
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
                return new AsyncResult<>(1);
            }
        }

        return new AsyncResult<>(-1);
    }

    @Async
    public ListenableFuture<Integer> repositoryCondition(Long projectId, String repositoryName, Condition condition) {
        logger.info("封装应用服务查询, 异步开启");
        // 调用外部接口模糊查询 应用服务
        if (!StringUtils.isEmpty(repositoryName)) {
            Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnProjectLevel(projectId, repositoryName);

            if (repositoryIdSet.isEmpty()) {
                return new AsyncResult<>(0);
            } else {
                condition.and().andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
                return new AsyncResult<>(1);
            }
        }

        return new AsyncResult<>(-1);
    }

    @Async
    public ListenableFuture<Integer> paramsCondition(Long projectId, String params, Condition condition) {
        logger.info("封装通用查询, 异步开启");
        // 根据params多条件查询
        if (!StringUtils.isEmpty(params)) {

            FutureTask<Set<Long>> futureTask1 = new FutureTask<>(() -> {
                Set<Long> userIdsSet1 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, params, null);
                return userIdsSet1;
            });

            FutureTask<Set<Long>> futureTask2 = new FutureTask<>(() -> {
                Set<Long> userIdsSet2 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, null, params);
                return userIdsSet2;
            });

            FutureTask<Set<Long>> futureTask3 = new FutureTask<>(() -> {
                Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnProjectLevel(projectId, params);
                return repositoryIdSet;
            });

            ExecutorService executorService = Executors.newFixedThreadPool(3);
            executorService.submit(futureTask1);
            executorService.submit(futureTask2);
            executorService.submit(futureTask3);

            Set<Long> userIdsSet1 = Collections.emptySet();
            Set<Long> userIdsSet2 = Collections.emptySet();
            Set<Long> repositoryIdSet = Collections.emptySet();
            try {
                userIdsSet1 = futureTask1.get();
                userIdsSet2 = futureTask2.get();
                repositoryIdSet = futureTask3.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Set<Long> userIdsSet = new HashSet<>();
            userIdsSet.addAll(userIdsSet1);
            userIdsSet.addAll(userIdsSet2);

//            Set<Long> userIdsSet1 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, params, null);
//            Set<Long> userIdsSet2 = c7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, null, params);
//            Set<Long> userIdsSet = new HashSet<>();
//            userIdsSet.addAll(userIdsSet1);
//            userIdsSet.addAll(userIdsSet2);
//
//            Set<Long> repositoryIdSet = c7NDevOpsServiceFacade.listC7nAppServiceIdsByNameOnProjectLevel(projectId, params);

            boolean userIsEmpty = userIdsSet.isEmpty();
            boolean repositoryIsEmpty = repositoryIdSet.isEmpty();

            if (userIsEmpty && repositoryIsEmpty) {
                // 都为空, 查询结果为空
                return new AsyncResult<>(0);
            } else if (!userIsEmpty && !repositoryIsEmpty) {
                // 都不为空, or条件查询
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet)
                        .orIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
            } else if (!userIsEmpty) {
                // 用户查询不为空
                condition.and().andIn(RdmMember.FIELD_USER_ID, userIdsSet);
            } else {
                // 应用服务查询不为空
                condition.and().andIn(RdmMember.FIELD_REPOSITORY_ID, repositoryIdSet);
            }

            return new AsyncResult<>(1);
        }

        return new AsyncResult<>(-1);
    }
}
