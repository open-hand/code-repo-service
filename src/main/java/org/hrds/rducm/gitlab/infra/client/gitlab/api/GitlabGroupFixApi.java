package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import java.util.Date;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

/**
 * Created by wangxiang on 2021/5/31
 */
@Repository
public class GitlabGroupFixApi {

    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabGroupFixApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public Member getMember(Integer glGroupId, Integer glUserId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getGroupApi()
                    .getMember(glGroupId, glUserId);
        } catch (GitLabApiException e) {
            // Gitlab查询到不存在的资源会返回404
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
    }

    public Member addMember(Object groupIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getGroupApi()
                    .addMember(groupIdOrPath, userId, accessLevel, expiresAt);
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());

            }
        }
    }

    public Member updateMember(Object groupIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            if (expiresAt == null) {
                return gitlab4jClient.getGitLabApi()
                        .getGroupApi()
                        .updateMember(groupIdOrPath, userId, accessLevel);
            } else {
                return gitlab4jClient.getGitLabApi()
                        .getGroupApi()
                        .updateMember(groupIdOrPath, userId, accessLevel, expiresAt);
            }

        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
    }

    public void removeMember(Integer glGroupId, Integer glUserId) {
        try {
            gitlab4jClient.getGitLabApi()
                    .getGroupApi()
                    .removeMember(glGroupId, glUserId);
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
    }

}
