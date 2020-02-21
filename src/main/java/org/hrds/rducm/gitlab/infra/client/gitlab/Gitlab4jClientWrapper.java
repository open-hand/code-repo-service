package org.hrds.rducm.gitlab.infra.client.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.springframework.stereotype.Component;

/**
 * 临时使用, 后续需删除 fixme
 */
@Component
public class Gitlab4jClientWrapper extends Gitlab4jClient {
    @Override
    public GitLabApi getGitLabApi() {
        // 获取当前用户的gitlab客户端
        return getGitLabApiUser(null);
    }
}
