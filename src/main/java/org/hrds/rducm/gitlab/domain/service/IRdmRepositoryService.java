package org.hrds.rducm.gitlab.domain.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;

import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public interface IRdmRepositoryService {
    /**
     * 查询代码库总览信息
     *
     * @param projectId     项目id
     * @param pageRequest
     * @param repositoryIds
     * @return
     */
    Page<RepositoryOverViewDTO> pageOverviewByOptions(Long projectId, PageRequest pageRequest, Set<Long> repositoryIds);
}
