package org.hrds.rducm.gitlab.app.service.impl;

import org.hrds.rducm.gitlab.app.service.GitlabRepositoryService;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
@Service
public class GitlabRepositoryServiceImpl implements GitlabRepositoryService {
    @Autowired
    private GitlabRepositoryRepository gitlabRepositoryRepository;

    /**
     * 查询所有[已启用]的服务
     * todo 临时使用,需对接外围接口
     * @return
     */
    @Override
    public List<GitlabRepository> listByActive(Long projectId) {
        List<GitlabRepository> gitlabRepositories = gitlabRepositoryRepository.selectAll();
        return gitlabRepositories;
    }
}
