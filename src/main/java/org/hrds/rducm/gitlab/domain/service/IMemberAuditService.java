package org.hrds.rducm.gitlab.domain.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/2
 */
public interface IMemberAuditService {
    /**
     * 审计一个组织的成员
     * 1. 保存审计记录
     * 2. 记录审计日志
     *
     * @param organizationId
     */
    void auditMembersByOrganizationId(Long organizationId);
}
