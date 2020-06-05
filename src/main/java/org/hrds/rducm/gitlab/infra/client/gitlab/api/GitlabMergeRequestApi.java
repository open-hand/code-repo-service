package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.hrds.rducm.gitlab.infra.client.gitlab.exception.GitlabClientException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Repository
public class GitlabMergeRequestApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabMergeRequestApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    /**
     * 获取MergeRequests
     *
     * @param projectId 项目id
     * @param state     状态
     * @return
     */
    public List<MergeRequest> getMergeRequests(Integer projectId, Constants.MergeRequestState state) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getMergeRequestApi()
                    .getMergeRequests(projectId, state);
        } catch (GitLabApiException e) {
            throw new GitlabClientException(e, e.getMessage());
        }
    }
}
