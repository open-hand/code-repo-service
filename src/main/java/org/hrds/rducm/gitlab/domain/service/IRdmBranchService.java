package org.hrds.rducm.gitlab.domain.service;

import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public interface IRdmBranchService {
    /**
     * 获取分支列表(排除保护分支)
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    List<BranchDTO> getBranchesWithExcludeProtected(Long projectId, Long repositoryId);
}
