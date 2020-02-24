package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import io.choerodon.core.exception.CommonException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class GitlabUserApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabUserApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public User getUser(Integer userId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getUserApi()
                    .getUser(userId);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public User getUser(String username) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getUserApi()
                    .getUser(username);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public User createUser(User user, String password, Integer projectsLimit) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getUserApi()
                    .createUser(user, password, projectsLimit);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public User modifyUser(User user, String password, Integer projectsLimit) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getUserApi()
                    .modifyUser(user, password, projectsLimit);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }
}
