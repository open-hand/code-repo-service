package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * 成员权限审计日志表 资源库实现
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@Component
public class MemberAuditLogRepositoryImpl extends BaseRepositoryImpl<MemberAuditLog> implements MemberAuditLogRepository {

    @Override
    public int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId) {
        AssertUtils.notNull(organizationId, "organizationId not null");
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");

        MemberAuditLog param = new MemberAuditLog();
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        return this.delete(param);
    }
}
