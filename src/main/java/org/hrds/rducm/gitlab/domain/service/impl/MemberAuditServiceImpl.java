package org.hrds.rducm.gitlab.domain.service.impl;

import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/2
 */

@Service
public class MemberAuditServiceImpl implements IMemberAuditService {
    @Autowired
    private IRdmMemberAuditRecordService iRdmMemberAuditRecordService;
    @Autowired
    private MemberAuditLogRepository memberAuditLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMembersByOrganizationId(Long organizationId) {
        // <1> 保存审计记录
        Date startDate = new Date();
        List<RdmMemberAuditRecord> records = iRdmMemberAuditRecordService.batchCompare(organizationId);
        Date endDate = new Date();

        // <2> 记录审计日志
        // 按项目分组
        Map<Long, List<RdmMemberAuditRecord>> group = records.stream().collect(Collectors.groupingBy(RdmMemberAuditRecord::getProjectId));

        // 插入整个组织的审计日志
        String auditNo = UUID.randomUUID().toString();

        MemberAuditLog memberAuditLog = new MemberAuditLog();
        memberAuditLog.setOrganizationId(organizationId);
        memberAuditLog.setAuditNo(auditNo);
        memberAuditLog.setAuditCount(records.size());
        memberAuditLog.setAuditStartDate(startDate);
        memberAuditLog.setAuditEndDate(endDate);
        memberAuditLog.setAuditDuration(Math.toIntExact(Duration.between(startDate.toInstant(), endDate.toInstant()).toMillis()));
        memberAuditLogRepository.insertSelective(memberAuditLog);

        // 插入每个项目的审计日志
        group.forEach((key, value) -> {
            MemberAuditLog log = new MemberAuditLog();
            log.setOrganizationId(organizationId);
            log.setProjectId(key);
            log.setAuditNo(auditNo);
            log.setAuditCount(value.size());
            log.setAuditStartDate(startDate);
            log.setAuditEndDate(endDate);
            log.setAuditDuration(Math.toIntExact(Duration.between(startDate.toInstant(), endDate.toInstant()).toMillis()));
            memberAuditLogRepository.insertSelective(log);
        });
    }
}
