package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class GitlabPorjectApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabPorjectApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public List<Member> getMembers(Integer projectId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .getMembers(projectId);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .addMember(projectIdOrPath, userId, accessLevel, expiresAt);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public Member updateMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            if (expiresAt == null) {
                return gitlab4jClient.getGitLabApi()
                        .getProjectApi()
                        .updateMember((Integer) projectIdOrPath, userId, accessLevel);
            } else {
                return gitlab4jClient.getGitLabApi()
                        .getProjectApi()
                        .updateMember(projectIdOrPath, userId, accessLevel, expiresAt);
            }

        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public void removeMember(Integer projectId, Integer userId) {
        try {
            gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .removeMember(projectId, userId);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

}
