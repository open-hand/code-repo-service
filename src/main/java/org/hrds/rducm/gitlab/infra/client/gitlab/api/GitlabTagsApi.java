package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProtectedTag;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GitlabTagsApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabTagsApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public ProtectedTag getProtectedTag(Object projectIdOrPath, String tagName) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getTagsApi()
                    .getProtectedTag(projectIdOrPath, tagName);
        } catch (GitLabApiException e) {
            // Gitlab查询到不存在的资源会返回404
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
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
