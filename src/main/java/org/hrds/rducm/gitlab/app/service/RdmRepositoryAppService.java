package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.domain.entity.RdmRepository;

import java.util.List;

/**
 * 应用服务
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
public interface RdmRepositoryAppService {
    /**
     * 查询所有[已启用]的服务
     *
     * @param projectId
     * @return
     */
    List<RdmRepository> listByActive(Long projectId);
}
