package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 成员权限审计日志表资源库
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
public interface MemberAuditLogRepository extends BaseRepository<MemberAuditLog> {
    /**
     * 删除指定代码库的记录
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @return
     */
    int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId);
}
