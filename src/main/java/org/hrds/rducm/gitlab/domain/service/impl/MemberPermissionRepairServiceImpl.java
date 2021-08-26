package org.hrds.rducm.gitlab.domain.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.app.eventhandler.gitlab.GitlabPermissionRepair;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.app.service.RdmMemberAuditAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberPermissionRepairService;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
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
    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private Map<String, GitlabPermissionRepair> permissionRepairMap;

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
                //已经同步过的不在同步
                if (record.getSyncFlag()) {
                    return;
                }
                permissionRepairMap.get(record.getType()).gitlabPermissionRepair(record);
                rdmMemberAuditAppService.auditFix(record);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void membersBatchSyncJob(Long tenantId) {
        Set<Long> projectIds = c7NBaseServiceFacade.listProjectIds(tenantId);
        if (CollectionUtils.isEmpty(projectIds)) {
            return;
        }
        projectIds.forEach((projectId) -> {
            //查询项目下未同步的用户
            RdmMemberQueryDTO rdmMemberQueryDTO = new RdmMemberQueryDTO();
            rdmMemberQueryDTO.setSyncGitlabFlag(Boolean.FALSE);
            List<RdmMemberViewDTO> rdmMemberViewDTOS = rdmMemberAppService.listByOptions(projectId, rdmMemberQueryDTO);
            if (CollectionUtils.isEmpty(rdmMemberViewDTOS)) {
                return;
            }
            rdmMemberViewDTOS.forEach(rdmMemberViewDTO -> {
                if (StringUtils.equalsIgnoreCase(rdmMemberViewDTO.getType(), AuthorityTypeEnum.PROJECT.getValue())) {
                    rdmMemberAppService.syncMember(rdmMemberViewDTO.getId());
                } else if (StringUtils.equalsIgnoreCase(rdmMemberViewDTO.getType(), AuthorityTypeEnum.GROUP.getValue())) {
                    rdmMemberAppService.syncGroupMember(rdmMemberViewDTO.getId());
                } else {
                    return;
                }
            });
        });
    }
}
