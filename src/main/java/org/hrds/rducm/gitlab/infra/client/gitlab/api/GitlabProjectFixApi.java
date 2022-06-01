package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.constant.GitlabClientConstants;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public class GitlabProjectFixApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabProjectFixApi.class);

    public GitlabProjectFixApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public Member getMember(Integer projectId, Integer userId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .getMember(projectId, userId);
        } catch (GitLabApiException e) {
            // Gitlab查询到不存在的资源会返回404
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
    }

    public Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            if (expiresAt != null
                    && expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                    .isBefore(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
                return null;
            }
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .addMember(projectIdOrPath, userId, accessLevel, expiresAt);
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else if (e.getHttpStatus() == HttpStatus.BAD_REQUEST.value()) {
                LOGGER.info(">>>>>>>>>>>>>>>Bad Request:{},{},{}>>>>>>>>>>>>>>>>>", (Integer) projectIdOrPath, userId, accessLevel);
                return null;
            } else if (e.getHttpStatus() == HttpStatus.CONFLICT.value()) {
                LOGGER.info("Member already exists");
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());

            }
        }
    }

    public Member updateMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            if (expiresAt == null) {
                return gitlab4jClient.getGitLabApi()
                        .getProjectApi()
                        .updateMember((Integer) projectIdOrPath, userId, accessLevel);
            } else {
                if (expiresAt != null
                        && expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        .isBefore(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
                    return null;
                }
                return gitlab4jClient.getGitLabApi()
                        .getProjectApi()
                        .updateMember(projectIdOrPath, userId, accessLevel, expiresAt);
            }

        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else if (e.getHttpStatus() == HttpStatus.BAD_REQUEST.value()) {
                LOGGER.info(">>>>>>>>>>>>>>>Bad Request:{},{},{}>>>>>>>>>>>>>>>>>", (Integer) projectIdOrPath, userId, accessLevel);
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
    }

    public void removeMember(Integer projectId, Integer userId) {
        try {
            gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .removeMember(projectId, userId);
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                LOGGER.error("remove gitlab member 404 :", e.getMessage());
                return;
            } else if (e.getHttpStatus() == HttpStatus.FORBIDDEN.value()) {
                LOGGER.error("remove gitlab member 403 :", e.getMessage());
                return;
            } else {
                LOGGER.error("remove gitlab member :", e.getMessage());
                return;
            }
        }
    }

    /**
     * 获取项目详情
     * GET /projects/:id
     *
     * @param projectId 项目id
     * @return Project
     */
    public Project getProject(Integer projectId) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .getProject(projectId);
        } catch (GitLabApiException e) {
            // Gitlab查询到不存在的资源会返回404
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                return null;
            } else {
                throw new GitlabClientException(e, e.getMessage());
            }
        }
    }


}
