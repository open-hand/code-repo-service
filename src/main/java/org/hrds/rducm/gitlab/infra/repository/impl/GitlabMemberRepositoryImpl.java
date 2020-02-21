package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.domain.repository.GitlabMemberRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class GitlabMemberRepositoryImpl extends BaseRepositoryImpl<GitlabMember> implements GitlabMemberRepository {


}
