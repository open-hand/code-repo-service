package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.IC7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberAuditService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberAuditRecordService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
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
    @Autowired
    private IC7nBaseServiceFacade ic7NBaseServiceFacade;

    @Override
    public MemberAuditLog detailLatestAuditLog(Long organizationId, Long projectId) {
        Condition condition = Condition.builder(MemberAuditLog.class)
                .where(Sqls.custom()
                        .andEqualTo(MemberAuditLog.FIELD_ORGANIZATION_ID, organizationId)
                        .andEqualTo(MemberAuditLog.FIELD_PROJECT_ID, projectId))
                .orderByDesc(MemberAuditLog.FIELD_CREATION_DATE)
                .build();
        Page<MemberAuditLog> page = PageHelper.doPage(0, 1, () -> memberAuditLogRepository.selectByCondition(condition));

        return page.getContent().isEmpty() ? new MemberAuditLog() : page.getContent().get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMembersByOrganizationId(Long organizationId) {
        // <1> 保存审计记录
        Date startDate = new Date();
        List<RdmMemberAuditRecord> records = iRdmMemberAuditRecordService.batchCompare(organizationId);
        Date endDate = new Date();

        // <2> 记录审计日志
        // 获取组织下所有项目
        Set<Long> projectIds = ic7NBaseServiceFacade.listProjectIds(organizationId);

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
        projectIds.forEach((projectId) -> {
            List<RdmMemberAuditRecord> list = group.get(projectId);

            MemberAuditLog log = new MemberAuditLog();
            log.setOrganizationId(organizationId);
            log.setProjectId(projectId);
            log.setAuditNo(auditNo);
            log.setAuditCount(list == null ? 0 : list.size());
            log.setAuditStartDate(startDate);
            log.setAuditEndDate(endDate);
            log.setAuditDuration(Math.toIntExact(Duration.between(startDate.toInstant(), endDate.toInstant()).toMillis()));
            memberAuditLogRepository.insertSelective(log);
        });
    }
}
