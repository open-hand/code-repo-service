package org.hrds.rducm.gitlab.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.OperationLogViewDTO;

import java.util.Set;

/**
 * 操作日志表应用服务
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
public interface RdmOperationLogAppService {
    /**
     * 项目层
     * 按条件查询成员操作日志
     *
     * @param projectId
     * @param repositoryIds
     * @param pageRequest
     * @param queryDTO
     * @return
     */
    PageInfo<OperationLogViewDTO> pageByOptionsMemberLog(Long projectId,
                                                         Set<Long> repositoryIds,
                                                         PageRequest pageRequest,
                                                         OperationLogQueryDTO queryDTO);

    /**
     * 组织层
     * 按条件查询成员操作日志
     *
     * @param organizationId
     * @param projectIds
     * @param repositoryIds
     * @param pageRequest
     * @param queryDTO
     * @return
     */
    PageInfo<OperationLogViewDTO> pageByOptionsMemberLogOnOrg(Long organizationId,
                                                              Set<Long> projectIds,
                                                              Set<Long> repositoryIds,
                                                              PageRequest pageRequest,
                                                              OperationLogQueryDTO queryDTO);
}
