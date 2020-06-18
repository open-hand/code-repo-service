package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberAuditRecordMapper;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 成员权限审计记录表 资源库实现
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
@Component
public class RdmMemberAuditRecordRepositoryImpl extends BaseRepositoryImpl<RdmMemberAuditRecord> implements RdmMemberAuditRecordRepository {
    @Autowired
    private RdmMemberAuditRecordMapper rdmMemberAuditRecordMapper;

    @Override
    public RdmMemberAuditRecord selectByUk(Long organizationId, Long projectId, Long repositoryId, Long id) {
        AssertUtils.notNull(organizationId, "organizationId not null");
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");
        AssertUtils.notNull(id, "id not null");

        RdmMemberAuditRecord param = new RdmMemberAuditRecord();
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        param.setId(id);
        return this.selectOne(param);
    }

    @Override
    public int updateSyncTrueByPrimaryKeySelective(RdmMemberAuditRecord record) {
        record.setSyncFlag(Boolean.TRUE);
        return this.updateByPrimaryKeySelective(record);
    }

    @Override
    public int batchInsertCustom(List<RdmMemberAuditRecord> list) {
        return rdmMemberAuditRecordMapper.batchInsertCustom(list);
    }

    @Override
    public int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId) {
        AssertUtils.notNull(organizationId, "organizationId not null");
        AssertUtils.notNull(projectId, "projectId not null");
        AssertUtils.notNull(repositoryId, "repositoryId not null");

        RdmMemberAuditRecord param = new RdmMemberAuditRecord();
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setRepositoryId(repositoryId);
        return this.delete(param);
    }
}
