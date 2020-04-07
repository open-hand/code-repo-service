package org.hrds.rducm.gitlab.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberAuditRecordViewDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

import java.util.List;
import java.util.Set;

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
     * @param repositoryIds
     *
     * @return
     */
    PageInfo<RdmMemberAuditRecordViewDTO> pageByOptions(Long organizationId, Long projectId, PageRequest pageRequest, Set<Long> repositoryIds);

    /**
     * 比对组织下的所有应用服务的成员权限
     *
     * @param organizationId
     * @return
     */
    List<RdmMemberAuditRecord> batchCompare(Long organizationId);
}
