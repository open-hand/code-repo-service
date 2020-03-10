package org.hrds.rducm.gitlab.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;

import java.util.List;

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
    PageInfo<RepositoryOverViewDTO> pageOverviewByOptions(Long projectId, PageRequest pageRequest, List<Long> repositoryIds);
}
