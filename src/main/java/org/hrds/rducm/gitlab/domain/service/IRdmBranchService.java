package org.hrds.rducm.gitlab.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.repository.RepositoryOverViewDTO;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public interface IRdmBranchService {
    /**
     * 获取分支列表(排除保护分支)
     *
     * @param repositoryId
     * @return
     */
    List<BranchDTO> getBranchesWithExcludeProtected(Long repositoryId);
}
