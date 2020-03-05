package org.hrds.rducm.gitlab.infra.repository.impl;

import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabUserApi;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class GitlabUserRepositoryImpl extends BaseRepositoryImpl<RdmUser> implements GitlabUserRepository {
    @Autowired
    private GitlabUserApi gitlabUserApi;

    @Override
    public RdmUser selectByUk(Long userId) {
        RdmUser rdmUser = new RdmUser();
        rdmUser.setUserId(userId);
        rdmUser = this.selectOne(rdmUser);
        return rdmUser;
    }

    @Override
    public User getUserFromGitlab(Integer userId) {
        return gitlabUserApi.getUser(userId);
    }

    @Override
    public User getUserFromGitlab(String username) {
        return gitlabUserApi.getUser(username);
    }

    @Override
    public User createUserToGitlab(String email, String username, String name, String password) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setName(name);
        return gitlabUserApi.createUser(user, password, null);
    }

    @Override
    public User updateUserPasswordToGitlab(Long userId, String password) {
        User user = new User();
        user.setId(Objects.requireNonNull(userId).intValue());
        return gitlabUserApi.modifyUser(user, Objects.requireNonNull(password), null);
    }
}
