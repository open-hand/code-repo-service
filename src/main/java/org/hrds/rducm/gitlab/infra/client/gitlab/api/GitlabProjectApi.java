package org.hrds.rducm.gitlab.infra.client.gitlab.api;

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

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class GitlabProjectApi {
    private final Gitlab4jClientWrapper gitlab4jClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public GitlabProjectApi(Gitlab4jClientWrapper gitlab4jClient) {
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

    public List<Member> getMembers(Integer projectId) {
        try {
            // 需要查询所有成员
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .getMembers(projectId, GitlabClientConstants.DEFAULT_PER_PAGE)
                    .all();
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    /**
     * 获取项目单个成员, 包括继承的成员
     * 由于当前Gitlab版本不支持
     *
     * @param projectId
     * @param userId    Gitlab用户id
     * @return
     */
    public Member getAllMember(Integer projectId, Integer userId) {
        try {
            User glUser = gitlab4jClient.getGitLabApi().getUserApi().getUser(userId);
            return this.getAllMember(projectId, Objects.requireNonNull(glUser.getUsername()));
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    /**
     * 获取项目所有成员, 包括继承的成员
     *
     * @param projectId
     * @return
     */
    public List<Member> getAllMembers(Integer projectId) {
        try {
            // 需要查询所有成员
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .getAllMembers(projectId, GitlabClientConstants.DEFAULT_PER_PAGE, null)
                    .all();
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
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                logger.info(">>>>>>>NOT_FOUND:projectId:{},userId:{}>>>>>>>>>>>>>", projectId, userId);
            } else {
                throw new GitlabClientException(e, e.getMessage());

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

    /* private方法 */

    /**
     * 获取项目单个成员, 包括继承的成员
     * 由于当前Gitlab版本不支持
     *
     * @param projectId
     * @param userName  用户名
     * @return
     */
    private Member getAllMember(Integer projectId, String userName) {
        try {
            // 需要查询所有成员
            List<Member> allMembers = gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .getAllMembers(projectId, userName);
            if (allMembers.isEmpty()) {
                return null;
            } else if (allMembers.size() == 1) {
                return allMembers.get(0);
            } else {
                // 获取username匹配的那个成员
                return allMembers.stream()
                        .filter(member -> member.getUsername().equals(userName))
                        .findFirst()
                        .orElse(null);
            }
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }
}
