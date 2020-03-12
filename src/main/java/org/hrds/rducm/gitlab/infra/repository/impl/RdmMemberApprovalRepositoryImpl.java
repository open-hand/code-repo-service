package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApprovalRepository;
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
public class RdmMemberApprovalRepositoryImpl extends BaseRepositoryImpl<RdmMemberApproval> implements RdmMemberApprovalRepository {
    @Override
    public RdmMemberApproval selectOneWithPending(Long projectId, Long repositoryId, Long applicantUserId) {
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");
        AssertUtils.notNull(applicantUserId, "applicantUserId not null");

        RdmMemberApproval rdmMemberApproval = new RdmMemberApproval();
        // 只允许有一条待审批的记录
        rdmMemberApproval.setApprovalState(ApprovalStateEnum.PENDING.getCode());
        rdmMemberApproval.setProjectId(projectId);
        rdmMemberApproval.setRepositoryId(repositoryId);
        rdmMemberApproval.setApplicantUserId(applicantUserId);
        return this.selectOne(rdmMemberApproval);
    }
  
}
