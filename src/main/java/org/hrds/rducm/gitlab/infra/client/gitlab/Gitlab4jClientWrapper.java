package org.hrds.rducm.gitlab.infra.client.gitlab;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.choerodon.core.oauth.DetailsHelper;
import org.gitlab4j.api.GitLabApi;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 封装Gitlab4jClient的功能
 */
@Component
public class Gitlab4jClientWrapper {
    /**
     * 使用guava做一个缓存
     * 24小时过期
     * 最大缓存数量为1000
     */
    private static final Cache<Long, Integer> USER_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();

    private static final Logger LOGGER = LoggerFactory.getLogger(Gitlab4jClientWrapper.class);

    @Autowired
    private Gitlab4jClient gitlab4jClient;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    /**
     * 获取当前用户权限的GitlabApi
     *
     * @return GitLabApi
     */
    public GitLabApi getGitLabApi() {
        // 转换Gitlab userId
        Long userId = DetailsHelper.getUserDetails().getUserId();

        // 获取Gitlab用户id, 如果缓存有, 从缓存取;否则调接口取
        Integer glUserId;
        if (USER_CACHE.getIfPresent(userId) != null) {
            glUserId = USER_CACHE.getIfPresent(userId);
            LOGGER.debug("使用缓存中的用户 {}对应的Gitlab用户ID {}", userId, glUserId);
        } else {
            glUserId = Objects.requireNonNull(c7NBaseServiceFacade.userIdToGlUserId(userId));
            USER_CACHE.put(userId, glUserId);
        }

        // 获取当前用户的gitlab客户端
        return gitlab4jClient.getGitLabApiUser(glUserId);
    }

    /**
     * 获取管理员权限的GitLabApi
     *
     * @return GitLabApi
     */
    public GitLabApi getAdminGitLabApi() {
        // 获取管理员的gitlab客户端
        return gitlab4jClient.getGitLabApi();
    }
}
