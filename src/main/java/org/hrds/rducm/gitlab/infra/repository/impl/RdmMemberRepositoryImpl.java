package org.hrds.rducm.gitlab.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;
import org.springframework.validation.ValidationUtils;

import java.util.Date;

@Repository
public class RdmMemberRepositoryImpl extends BaseRepositoryImpl<RdmMember> implements RdmMemberRepository {
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

//    /**
//     * 检查当前记录是否处于"预更新"状态
//     *
//     * @param m
//     */
//    @Override
//    public void checkIsSyncGitlab(RdmMember m) {
//        if (!m.getSyncGitlabFlag()) {
//            // 当同步标记为false时, 表示上个事务还未结束
//            throw new CommonException("error.sync.flag.false");
//        }
//    }
}
