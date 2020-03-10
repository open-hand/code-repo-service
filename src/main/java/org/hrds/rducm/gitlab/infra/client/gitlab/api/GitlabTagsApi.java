package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProtectedTag;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GitlabTagsApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabTagsApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public List<ProtectedTag> getProtectedTags(Object projectIdOrPath) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getTagsApi()
                    .getProtectedTags(projectIdOrPath);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public ProtectedTag protectTag(Object projectIdOrPath, String name, AccessLevel createAccessLevel) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getTagsApi()
                    .protectTag(projectIdOrPath, name, createAccessLevel);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public void unprotectTag(Object projectIdOrPath, String name) {
        try {
            gitlab4jClient.getGitLabApi()
                    .getTagsApi()
                    .unprotectTag(projectIdOrPath, name);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }
}
