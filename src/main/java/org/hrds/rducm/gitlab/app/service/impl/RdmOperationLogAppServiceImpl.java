package org.hrds.rducm.gitlab.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.app.assembler.RdmOperationLogAssembler;
import org.hrds.rducm.gitlab.app.service.RdmOperationLogAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.domain.repository.RdmOperationLogRepository;
import org.hrds.rducm.gitlab.infra.audit.event.AbstractOperationEvent;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 操作日志表应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
@Service
public class RdmOperationLogAppServiceImpl implements RdmOperationLogAppService {
    @Autowired
    private RdmOperationLogRepository operationLogRepository;
    @Autowired
    private RdmOperationLogAssembler rdmOperationLogAssembler;

    @Override
    public PageInfo<OperationLogViewDTO> pageByOptionsMemberLog(Long projectId,
                                                                Set<Long> repositoryIds,
                                                                PageRequest pageRequest,
                                                                OperationLogQueryDTO queryDTO) {
        return pageByOptionsMemberLogCommon(null, Collections.singleton(projectId), repositoryIds, pageRequest, queryDTO);
    }

    @Override
    public PageInfo<OperationLogViewDTO> pageByOptionsMemberLogOnOrg(Long organizationId,
                                                                     Set<Long> projectIds,
                                                                     Set<Long> repositoryIds,
                                                                     PageRequest pageRequest,
                                                                     OperationLogQueryDTO queryDTO) {
        return pageByOptionsMemberLogCommon(Collections.singleton(organizationId), projectIds, repositoryIds, pageRequest, queryDTO);
    }

    /**
     * 查询成员操作日志(可复用)
     *
     * @param organizationIds
     * @param projectIds
     * @param repositoryIds
     * @param pageRequest
     * @param queryDTO
     * @return
     */
    private PageInfo<OperationLogViewDTO> pageByOptionsMemberLogCommon(Set<Long> organizationIds,
                                                                       Set<Long> projectIds,
                                                                       Set<Long> repositoryIds,
                                                                       PageRequest pageRequest,
                                                                       OperationLogQueryDTO queryDTO) {
        Long opUserId = queryDTO.getOpUserId();
        Date startDate = queryDTO.getStartDate();
        Date endDate = queryDTO.getEndDate();
        List<String> opEventTypes = queryDTO.getOpEventTypes();

        String opType = AbstractOperationEvent.OperationType.MEMBER_MANAGEMENT.getCode();

        // 封装查询条件
        Sqls sqls = Sqls.custom()
                .andIn(RdmOperationLog.FIELD_ORGANIZATION_ID, organizationIds, true)
                .andIn(RdmOperationLog.FIELD_PROJECT_ID, projectIds, true)
                .andIn(RdmOperationLog.FIELD_REPOSITORY_ID, repositoryIds, true)
                .andEqualTo(RdmOperationLog.FIELD_OP_USER_ID, opUserId, true)
                .andIn(RdmOperationLog.FIELD_OP_EVENT_TYPE, opEventTypes, true)
                .andEqualTo(RdmOperationLog.FIELD_OP_TYPE, opType);

        if (startDate != null) {
            sqls.andGreaterThanOrEqualTo(RdmOperationLog.FIELD_OP_DATE, startDate);
        }

        if (endDate != null) {
            sqls.andLessThan(RdmOperationLog.FIELD_OP_DATE, endDate);
        }

        Condition condition = Condition.builder(RdmOperationLog.class)
                .where(sqls)
                .build();

        Page<RdmOperationLog> page = PageHelper.doPageAndSort(pageRequest, () -> operationLogRepository.selectByCondition(condition));

        return rdmOperationLogAssembler.pageToOperationLogViewDTO(page);
    }
}
