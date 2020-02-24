package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import io.choerodon.core.exception.CommonException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GitlabProtectedBranchesApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabProtectedBranchesApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public List<ProtectedBranch> getProtectedBranches(Object projectIdOrPath) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProtectedBranchesApi()
                    .getProtectedBranches(projectIdOrPath);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public ProtectedBranch protectBranch(Object projectIdOrPath, String branchName, AccessLevel pushAccessLevel, AccessLevel mergeAccessLevel) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProtectedBranchesApi()
                    .protectBranch(projectIdOrPath, branchName, pushAccessLevel, mergeAccessLevel);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public void unprotectBranch(Object projectIdOrPath, String branchName) {
        try {
            gitlab4jClient.getGitLabApi()
                    .getProtectedBranchesApi()
                    .unprotectBranch(projectIdOrPath, branchName);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }
}
