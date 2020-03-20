package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApplicantRepository;
import org.hrds.rducm.gitlab.infra.enums.ApprovalStateEnum;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * 成员审批表 资源库实现
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
@Component
public class RdmMemberApplicantRepositoryImpl extends BaseRepositoryImpl<RdmMemberApplicant> implements RdmMemberApplicantRepository {
    @Override
    public RdmMemberApplicant selectOneWithPending(Long projectId, Long repositoryId, Long applicantUserId) {
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");
        AssertUtils.notNull(applicantUserId, "applicantUserId not null");

        RdmMemberApplicant rdmMemberApplicant = new RdmMemberApplicant();
        // 只允许有一条待审批的记录
        rdmMemberApplicant.setApprovalState(ApprovalStateEnum.PENDING.getCode());
        rdmMemberApplicant.setProjectId(projectId);
        rdmMemberApplicant.setRepositoryId(repositoryId);
        rdmMemberApplicant.setApplicantUserId(applicantUserId);
        return this.selectOne(rdmMemberApplicant);
    }
  
}
