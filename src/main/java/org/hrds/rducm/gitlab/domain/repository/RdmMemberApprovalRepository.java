package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 成员审批表资源库
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
public interface RdmMemberApprovalRepository extends BaseRepository<RdmMemberApproval> {
    /**
     * 查询唯一的待审批的记录
     *
     * @param projectId
     * @param repositoryId
     * @param applicantUserId
     * @return
     */
    RdmMemberApproval selectOneWithPending(Long projectId, Long repositoryId, Long applicantUserId);
}
