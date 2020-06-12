package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
    public int deleteByProjectIdAndUserId(Long organizationId, Long projectId, Long userId) {
        AssertUtils.notNull(organizationId, "organizationId not null");
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(userId, "userId not null");

        RdmMember param = new RdmMember();
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setUserId(userId);
        return this.delete(param);
    }

    @Override
    public int deleteByOrganizationIdAndUserId(Long organizationId, Long userId) {
        AssertUtils.notNull(organizationId, "organizationId not null");
        AssertUtils.notNull(userId, "userId not null");

        RdmMember param = new RdmMember();
        param.setOrganizationId(organizationId);
        param.setUserId(userId);
        return this.delete(param);
    }

    @Override
    public int insertWithOwner(Long organizationId, Long projectId, Long repositoryId, Long userId, Integer glProjectId, Integer glUserId) {
        RdmMember param = new RdmMember();
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        param.setUserId(userId);
        param.setGlProjectId(glProjectId);
        param.setGlUserId(glUserId);
        param.setSyncGitlabFlag(Boolean.TRUE);
        param.setGlAccessLevel(RdmAccessLevel.OWNER.toValue());
        param.setSyncGitlabDate(new Date());

        return this.insertSelective(param);
    }

    @Override
    public int selectCountByRepositoryId(Long repositoryId) {
        AssertUtils.notNull(repositoryId, "repositoryId not null");

        RdmMember rdmMember = new RdmMember();
        rdmMember.setRepositoryId(repositoryId);
        return this.selectCount(rdmMember);
    }

    @Override
    public int selectManagerCountByRepositoryId(Long repositoryId) {
        AssertUtils.notNull(repositoryId, "repositoryId not null");

        RdmMember rdmMember = new RdmMember();
        rdmMember.setRepositoryId(repositoryId);
        Condition condition = Condition.builder(RdmMember.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMember.FIELD_REPOSITORY_ID, repositoryId)
                        .andGreaterThanOrEqualTo(RdmMember.FIELD_GL_ACCESS_LEVEL, RdmAccessLevel.MAINTAINER.toValue()))
                .build();
        return this.selectCountByCondition(condition);
    }

    //
    // 不可复用方法
    // ------------------------------------------------------------------------------

    @Override
    public List<MemberAuthDetailAgg> selectMembersRepositoryAuthorized(Long organizationId, Long projectId, Set<Long> userIds) {
        return rdmMemberMapper.selectMembersRepositoryAuthorized(organizationId, projectId, userIds);
    }
}
