package org.hrds.rducm.gitlab.domain.service;

import java.util.List;

import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/2
 */
public interface IMemberAuditService {
    /**
     * 获取最近一条审计日志
     *
     * @param organizationId
     * @param projectId      projectId可为null
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
