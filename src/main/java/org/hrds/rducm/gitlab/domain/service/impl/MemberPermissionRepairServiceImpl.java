package org.hrds.rducm.gitlab.domain.service.impl;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberPermissionRepairService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author 14589 2020/10/26 16:37
 */
@Service
public class MemberPermissionRepairServiceImpl implements IMemberPermissionRepairService {

    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private RdmMemberAuditAppService rdmMemberAuditAppService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repairMemberPermission(Long organizationId) {
        List<RdmMemberAuditRecord> records = rdmMemberAuditRecordRepository
                .selectByCondition(Condition.builder(RdmMemberAuditRecord.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(RdmMemberAuditRecord.FIELD_ORGANIZATION_ID, organizationId)
                ).build());

        Set<Long> projectIds = c7NBaseServiceFacade.listProjectIds(organizationId);
        Map<Long, List<RdmMemberAuditRecord>> group = records.stream().collect(Collectors.groupingBy(RdmMemberAuditRecord::getProjectId));

        projectIds.forEach((projectId) -> {
            List<RdmMemberAuditRecord> list = group.get(projectId);
            for(RdmMemberAuditRecord record : list) {
                rdmMemberAuditAppService.auditFix(record.getOrganizationId(), record.getProjectId(), record.getRepositoryId(), record.getId());
            }
        });
    }
}
