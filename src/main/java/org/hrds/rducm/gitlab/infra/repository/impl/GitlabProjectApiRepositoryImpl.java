package org.hrds.rducm.gitlab.infra.repository.impl;

import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.domain.repository.GitlabProjectApiRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabPorjectApi;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Objects;

/**
 * Repository Impl
 */
@Repository
public class GitlabProjectApiRepositoryImpl implements GitlabProjectApiRepository {
    private final GitlabPorjectApi gitlabPorjectApi;

    public GitlabProjectApiRepositoryImpl(GitlabPorjectApi gitlabPorjectApi) {
        this.gitlabPorjectApi = gitlabPorjectApi;
    }

    @Override
    public Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        return gitlabPorjectApi.addMember(projectIdOrPath, userId, accessLevel, expiresAt);
    }

    @Override
    public Member updateMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        return gitlabPorjectApi.updateMember(projectIdOrPath, userId, accessLevel, expiresAt);
    }

    @Override
    public void removeMember(Integer projectId, Integer userId) {
        gitlabPorjectApi.removeMember(projectId, userId);
    }
}
