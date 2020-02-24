package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.app.service.GitlabBranchService;
import org.hrds.rducm.gitlab.domain.repository.GitlabBranchRepository;
import org.hrds.rducm.gitlab.infra.enums.GitlabAccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitlabBranchServiceImpl implements GitlabBranchService {
    @Autowired
    private GitlabBranchRepository gitlabBranchRepository;

    @Override
    public List<ProtectedBranch> getProtectedBranches(Integer glProjectId) {
        return gitlabBranchRepository.getProtectedBranchesFromGitlab(glProjectId);
    }

    @Override
    public ProtectedBranch protectBranch(Integer glProjectId,
                                         String branchName,
                                         Integer pushAccessLevel,
                                         Integer mergeAccessLevel) {
        return gitlabBranchRepository.protectBranchToGitlab(glProjectId, branchName, pushAccessLevel, mergeAccessLevel);
    }

    @Override
    public void unprotectBranch(Integer glProjectId, String branchName) {
        gitlabBranchRepository.unprotectBranchToGitlab(glProjectId, branchName);
    }
}
