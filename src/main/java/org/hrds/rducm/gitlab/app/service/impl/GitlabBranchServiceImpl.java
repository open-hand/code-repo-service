package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.app.service.GitlabBranchService;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabBranchRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.hrds.rducm.gitlab.infra.enums.GitlabAccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitlabBranchServiceImpl implements GitlabBranchService {
    @Autowired
    private GitlabBranchRepository gitlabBranchRepository;
    @Autowired
    private GitlabRepositoryRepository repositoryRepository;

    @Override
    public List<Branch> getBranches(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return gitlabBranchRepository.getBranchesFromGitlab(gitlabRepository.getGlProjectId());
    }

    @Override
    public List<ProtectedBranch> getProtectedBranches(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return gitlabBranchRepository.getProtectedBranchesFromGitlab(gitlabRepository.getGlProjectId());
    }

    @Override
    public ProtectedBranch protectBranch(Long repositoryId,
                                         String branchName,
                                         Integer pushAccessLevel,
                                         Integer mergeAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return gitlabBranchRepository.protectBranchToGitlab(gitlabRepository.getGlProjectId(), branchName, pushAccessLevel, mergeAccessLevel);
    }

    @Override
    public void unprotectBranch(Long repositoryId, String branchName) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        gitlabBranchRepository.unprotectBranchToGitlab(gitlabRepository.getGlProjectId(), branchName);
    }
}
