package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GitlabRepositoryApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabRepositoryApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public List<Branch> getBranches(Integer projectId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getRepositoryApi()
                    .getBranches(projectId);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public List<Tag> getTags(Integer projectId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getRepositoryApi()
                    .getTags(projectId);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }
}
