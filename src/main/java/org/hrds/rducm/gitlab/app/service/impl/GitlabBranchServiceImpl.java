package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.ProtectedBranchDTO;
import org.hrds.rducm.gitlab.app.service.GitlabBranchService;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabBranchRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
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
    public List<BranchDTO> getBranches(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return ConvertUtils.convertList(gitlabBranchRepository.getBranchesFromGitlab(gitlabRepository.getGlProjectId()), BranchDTO.class);
    }

    @Override
    public List<ProtectedBranchDTO> getProtectedBranches(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return ConvertUtils.convertList(gitlabBranchRepository.getProtectedBranchesFromGitlab(gitlabRepository.getGlProjectId()), ProtectedBranchDTO.class);
    }

    @Override
    public ProtectedBranchDTO protectBranch(Long repositoryId,
                                            String branchName,
                                            Integer pushAccessLevel,
                                            Integer mergeAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        ProtectedBranch protectedBranch = gitlabBranchRepository.protectBranchToGitlab(gitlabRepository.getGlProjectId(), branchName, pushAccessLevel, mergeAccessLevel);
        return ConvertUtils.convertObject(protectedBranch, ProtectedBranchDTO.class);
    }

    @Override
    public ProtectedBranchDTO updateProtectedBranch(Long repositoryId,
                                                    String branchName,
                                                    Integer pushAccessLevel,
                                                    Integer mergeAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);

        // 由于Gitlab不提供修改保护分支的api, 故只能先删除, 再新增
        gitlabBranchRepository.unprotectBranchToGitlab(gitlabRepository.getGlProjectId(), branchName);
        ProtectedBranch protectedBranch = gitlabBranchRepository.protectBranchToGitlab(gitlabRepository.getGlProjectId(), branchName, pushAccessLevel, mergeAccessLevel);
        return ConvertUtils.convertObject(protectedBranch, ProtectedBranchDTO.class);
    }

    @Override
    public void unprotectBranch(Long repositoryId, String branchName) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        gitlabBranchRepository.unprotectBranchToGitlab(gitlabRepository.getGlProjectId(), branchName);
    }
}
