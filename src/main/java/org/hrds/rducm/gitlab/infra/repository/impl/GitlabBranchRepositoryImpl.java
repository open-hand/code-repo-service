package org.hrds.rducm.gitlab.infra.repository.impl;

import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.domain.repository.GitlabBranchRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProtectedBranchesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GitlabBranchRepositoryImpl implements GitlabBranchRepository {
    @Autowired
    private GitlabProtectedBranchesApi gitlabProtectedBranchesApi;

    @Override
    public List<ProtectedBranch> getProtectedBranchesFromGitlab(Object projectIdOrPath) {
        return gitlabProtectedBranchesApi.getProtectedBranches(projectIdOrPath);
    }

    @Override
    public ProtectedBranch protectBranchToGitlab(Object projectIdOrPath, String branchName, Integer pushAccessLevel, Integer mergeAccessLevel) {
        AccessLevel pushAccessLevelEnum = AccessLevel.forValue(pushAccessLevel);
        AccessLevel mergeAccessLevelEnum = AccessLevel.forValue(mergeAccessLevel);
        return gitlabProtectedBranchesApi.protectBranch(projectIdOrPath, branchName, pushAccessLevelEnum, mergeAccessLevelEnum);
    }

    @Override
    public void unprotectBranchToGitlab(Object projectIdOrPath, String branchName) {
        gitlabProtectedBranchesApi.unprotectBranch(projectIdOrPath, branchName);
    }
}
