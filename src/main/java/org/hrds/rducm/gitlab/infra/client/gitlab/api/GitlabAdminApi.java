package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.constant.GitlabClientConstants;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 供管理员调用的api
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/30
 */
@Repository
public class GitlabAdminApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabAdminApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    /**
     * 分页获取项目列表
     *
     * @return
     */
    public Pager<Project> getProjectsPageable() {
        try {
            return gitlab4jClient.getAdminGitLabApi()
                    .getProjectApi()
                    .getProjects(GitlabClientConstants.DEFAULT_PER_PAGE);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }

    public List<Member> getAllMembers(Integer projectId) {
        try {
            // 需要查询所有成员
            return gitlab4jClient.getAdminGitLabApi()
                    .getProjectApi()
                    .getAllMembers(projectId, GitlabClientConstants.DEFAULT_PER_PAGE, null)
                    .all();
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }
}
