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

    /**
     * 根据组织ID修复权限有问题的代码库成员
     * 1. 以代码库成员权限为准，修复问题数据
     * 2. 删除修复成功的审计数据
     * 3. 保留无法修复的审计数据
     *
     * @param organizationId
     * @param memberAuditRecordList
     */
    void repairMemberPermission(Long organizationId, List<RdmMemberAuditRecord> memberAuditRecordList);
}
