package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.springframework.stereotype.Component;

/**
 *  资源库实现
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
@Component
public class GitlabRepositoryRepositoryImpl extends BaseRepositoryImpl<GitlabRepository> implements GitlabRepositoryRepository {

    @Override
    public GitlabRepository selectByUk(Long repositoryId) {
        GitlabRepository repository = new GitlabRepository();
        repository.setRepositoryId(repositoryId);
        return this.selectOne(repository);
    }
  
}
