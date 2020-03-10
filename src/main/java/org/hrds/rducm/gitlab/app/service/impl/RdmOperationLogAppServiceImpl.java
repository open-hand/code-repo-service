package org.hrds.rducm.gitlab.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;
import org.hrds.rducm.gitlab.app.service.RdmOperationLogAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.domain.repository.RdmOperationLogRepository;
import org.hrds.rducm.gitlab.infra.audit.event.AbstractOperationEvent;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 操作日志表应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
@Service
public class RdmOperationLogAppServiceImpl implements RdmOperationLogAppService {
    @Autowired
    private RdmOperationLogRepository operationLogRepository;

    @Override
    public PageInfo<OperationLogViewDTO> pageByOptionsMemberLog(Long projectId, Long repositoryId, PageRequest pageRequest, OperationLogQueryDTO queryDTO) {
        Long opUserId = queryDTO.getOpUserId();
        Date startDate = queryDTO.getStartDate();
        Date endDate = queryDTO.getEndDate();

        String opType = AbstractOperationEvent.OperationType.MEMBER_MANAGEMENT.getCode();

        // 封装查询条件
        Sqls sqls = Sqls.custom()
                .andEqualTo(RdmOperationLog.FIELD_PROJECT_ID, projectId, true)
                .andEqualTo(RdmOperationLog.FIELD_REPOSITORY_ID, repositoryId, true)
                .andEqualTo(RdmOperationLog.FIELD_OP_USER_ID, opUserId, true)
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

        return PageConvertUtils.convert(ConvertUtils.convertPage(page, OperationLogViewDTO.class));
    }
}
