package org.hrds.rducm.gitlab.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;

/**
 * 操作日志表应用服务
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
public interface RdmOperationLogAppService {
    /**
     * 按条件查询成员操作日志
     *
     * @param projectId
     * @param repositoryId
     * @param pageRequest
     * @param queryDTO
     * @return
     */
    PageInfo<OperationLogViewDTO> pageByOptionsMemberLog(Long projectId, Long repositoryId, PageRequest pageRequest, OperationLogQueryDTO queryDTO);
}
