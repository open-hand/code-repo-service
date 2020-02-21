package org.hrds.rducm.gitlab.infra.repository.impl;

import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserApiRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabUserApi;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * Repository Impl
 */
@Repository
public class GitlabUserApiApiRepositoryImpl implements GitlabUserApiRepository {
    private final GitlabUserApi gitlabUserApi;

    public GitlabUserApiApiRepositoryImpl(GitlabUserApi gitlabUserApi) {
        this.gitlabUserApi = gitlabUserApi;
    }

    @Override
    public User getUser(Integer userId) {
        return gitlabUserApi.getUser(userId);
    }

    @Override
    public User createUser(String email, String username, String name, String password) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setName(name);
        return gitlabUserApi.createUser(user, password, null);
    }

    @Override
    public User updateUserPassword(Long userId, String password) {
        User user = new User();
        user.setId(Objects.requireNonNull(userId).intValue());
        return gitlabUserApi.modifyUser(user, Objects.requireNonNull(password), null);

    }
}
