package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.domain.repository.RdmOperationLogRepository;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * 操作日志表 资源库实现
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
@Component
public class RdmOperationLogRepositoryImpl extends BaseRepositoryImpl<RdmOperationLog> implements RdmOperationLogRepository {

    @Override
    public int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId) {
        AssertUtils.notNull(organizationId, "organizationId not null");
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");

        RdmOperationLog param = new RdmOperationLog();
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        return this.delete(param);
    }
}
