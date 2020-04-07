package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RdmMemberRepositoryImpl extends BaseRepositoryImpl<RdmMember> implements RdmMemberRepository {
    @Autowired
    private RdmMemberMapper rdmMemberMapper;

    @Override
    public RdmMember selectOneByUk(Long projectId, Long repositoryId, Long userId) {
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");
        AssertUtils.notNull(userId, "userId not null");

        RdmMember rdmMember = new RdmMember();
        rdmMember.setProjectId(projectId);
        rdmMember.setRepositoryId(repositoryId);
        rdmMember.setUserId(userId);
        return this.selectOne(rdmMember);
    }

    @Override
    public int selectCountByRepositoryId(Long repositoryId) {
        AssertUtils.notNull(repositoryId, "repositoryId not null");

        RdmMember rdmMember = new RdmMember();
        rdmMember.setRepositoryId(repositoryId);
        return this.selectCount(rdmMember);
    }

    //
    // 不可复用方法
    // ------------------------------------------------------------------------------

    @Override
    public List<MemberAuthDetailAgg> selectMembersRepositoryAuthorized(Long organizationId, Long projectId) {
        return rdmMemberMapper.selectMembersRepositoryAuthorized(organizationId, projectId);
    }
}
