package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.GitlabUser;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class GitlabUserRepositoryImpl extends BaseRepositoryImpl<GitlabUser> implements GitlabUserRepository {
}
