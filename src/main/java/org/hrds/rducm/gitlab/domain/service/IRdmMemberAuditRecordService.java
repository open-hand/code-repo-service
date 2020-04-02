package org.hrds.rducm.gitlab.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
public interface IRdmMemberAuditRecordService {
    /**
     * 分页查询权限审计记录
     *
     * @param organizationId
     * @param projectId
     * @param pageRequest
     * @return
     */
    PageInfo<RdmMemberAuditRecordViewDTO> pageByOptions(Long organizationId, Long projectId, PageRequest pageRequest);

    /**
     * 比对组织下的所有应用服务的成员权限
     *
     * @param organizationId
     * @return
     */
    List<RdmMemberAuditRecord> batchCompare(Long organizationId);
}
