package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 成员审批表资源库
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
public interface RdmMemberApplicantRepository extends BaseRepository<RdmMemberApplicant> {
    /**
     * 查询唯一的待审批的记录
     *
     * @param projectId
     * @param repositoryId
     * @param applicantUserId
     * @return
     */
    RdmMemberApplicant selectOneWithPending(Long projectId, Long repositoryId, Long applicantUserId);

    /**
     * 删除指定代码库下的记录
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @return
     */
    int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId);
}
