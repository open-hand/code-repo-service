package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Commit;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.stereotype.Repository;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Repository
public class GitlabCommitApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabCommitApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    /**
     * 获取最新一次提交
     *
     * @param projectId
     * @return
     */
    public Commit getLatestCommit(Integer projectId) {
        try {
            Pager<Commit> commits = gitlab4jClient.getGitLabApi()
                    .getCommitsApi()
                    .getCommits(projectId, 1);

            return commits.first().get(0);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }
}
