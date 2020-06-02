package org.hrds.rducm.gitlab.domain.service.impl;

import org.gitlab4j.api.models.Branch;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.domain.facade.IC7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmBranchRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmBranchService;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Service
public class RdmBranchServiceImpl implements IRdmBranchService {
    @Autowired
    private RdmBranchRepository rdmBranchRepository;
    @Autowired
    private IC7nDevOpsServiceFacade ic7NDevOpsServiceFacade;

    @Override
    public List<BranchDTO> getBranchesWithExcludeProtected(Long projectId, Long repositoryId) {
        // 获取对应Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);


        // 获取分支
        List<Branch> branches = rdmBranchRepository.getBranchesFromGitlab(glProjectId);

        // 排除保护分支
        branches.removeIf(Branch::getProtected);

        return ConvertUtils.convertList(branches, BranchDTO.class);
    }
}
