package org.hrds.rducm.gitlab.domain.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberPermissionRepairService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberPermissionRepairServiceImpl.class);

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
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Set<Long> projectIds = c7NBaseServiceFacade.listProjectIds(organizationId);
        if (CollectionUtils.isEmpty(projectIds)) {
            return;
        }
        Map<Long, List<RdmMemberAuditRecord>> group = records.stream().collect(Collectors.groupingBy(RdmMemberAuditRecord::getProjectId));
        if (MapUtils.isEmpty(group)) {
            return;
        }
        projectIds.forEach((projectId) -> {
            List<RdmMemberAuditRecord> list = group.get(projectId);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (RdmMemberAuditRecord record : list) {
                rdmMemberAuditAppService.auditFix(record.getOrganizationId(), record.getProjectId(), record.getRepositoryId(), record.getId());
            }
        });
    }
}
