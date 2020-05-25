package org.hrds.rducm.gitlab.infra.client.gitlab;

import io.choerodon.core.oauth.DetailsHelper;
import org.gitlab4j.api.GitLabApi;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 封装Gitlab4jClient的功能
 */
@Component
public class Gitlab4jClientWrapper implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private Gitlab4jClient gitlab4jClient;

    /**
     * 获取当前用户权限的GitlabApi
     *
     * @return GitLabApi
     */
    public GitLabApi getGitLabApi() {
        // 转换Gitlab userId
        Long userId = DetailsHelper.getUserDetails().getUserId();

        // 获取Gitlab用户id
        RdmUserRepository rdmUserRepository = applicationContext.getBean(RdmUserRepository.class);
        RdmUser dbUser = rdmUserRepository.selectByUk(userId);
        Integer glUserId = Objects.requireNonNull(dbUser.getGlUserId());

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
