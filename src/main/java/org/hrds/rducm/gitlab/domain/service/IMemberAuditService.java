package org.hrds.rducm.gitlab.domain.service;

import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/2
 */
public interface IMemberAuditService {
    /**
     * 获取最近一条审计日志
     *
     * @param organizationId
     * @param projectId
     * @return
     */
    MemberAuditLog detailLatestAuditLog(Long organizationId, Long projectId);

    /**
     * 审计一个组织的成员
     * 1. 保存审计记录
     * 2. 记录审计日志
     *
     * @param organizationId
     */
    void auditMembersByOrganizationId(Long organizationId);
}
