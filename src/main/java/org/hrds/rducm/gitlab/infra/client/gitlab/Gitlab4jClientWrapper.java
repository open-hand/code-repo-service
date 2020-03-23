package org.hrds.rducm.gitlab.infra.client.gitlab;

import io.choerodon.core.helper.ApplicationContextHelper;
import io.choerodon.core.oauth.DetailsHelper;
import org.gitlab4j.api.GitLabApi;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 临时使用, 后续需删除 fixme
 */
@Component
public class Gitlab4jClientWrapper extends Gitlab4jClient {
    @Override
    public GitLabApi getGitLabApi() {
        // 转换Gitlab userId todo
        Long userId = DetailsHelper.getUserDetails().getUserId();

        // 获取Gitlab用户id
        RdmUserRepository rdmUserRepository = ApplicationContextHelper.getApplicationContext().getBean(RdmUserRepository.class);
        RdmUser dbUser = rdmUserRepository.selectByUk(userId);
        Integer glUserId = Objects.requireNonNull(dbUser.getGlUserId());

        // 获取当前用户的gitlab客户端
        return getGitLabApiUser(glUserId);
    }
}
