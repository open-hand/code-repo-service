package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.ProtectedBranchDTO;
import org.hrds.rducm.gitlab.app.service.RdmBranchService;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmBranchRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RdmBranchServiceImpl implements RdmBranchService {
    @Autowired
    private RdmBranchRepository rdmBranchRepository;
    @Autowired
    private RdmRepositoryRepository repositoryRepository;

    @Override
    public List<BranchDTO> getBranches(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);
        return ConvertUtils.convertList(rdmBranchRepository.getBranchesFromGitlab(rdmRepository.getGlProjectId()), BranchDTO.class);
    }

    @Override
    public List<ProtectedBranchDTO> getProtectedBranches(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);

        List<ProtectedBranch> protectedBranches = rdmBranchRepository.getProtectedBranchesFromGitlab(rdmRepository.getGlProjectId());
        List<ProtectedBranchDTO> protectedBranchDTOS = ConvertUtils.convertList(protectedBranches, ProtectedBranchDTO.class);
        // 排序
        return protectedBranchDTOS.stream().sorted(Comparator.comparing(ProtectedBranchDTO::getName)).collect(Collectors.toList());
    }

    @Override
    public ProtectedBranchDTO protectBranch(Long repositoryId,
                                            String branchName,
                                            Integer pushAccessLevel,
                                            Integer mergeAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);
        ProtectedBranch protectedBranch = rdmBranchRepository.protectBranchToGitlab(rdmRepository.getGlProjectId(), branchName, pushAccessLevel, mergeAccessLevel);
        return ConvertUtils.convertObject(protectedBranch, ProtectedBranchDTO.class);
    }

    @Override
    public ProtectedBranchDTO updateProtectedBranch(Long repositoryId,
                                                    String branchName,
                                                    Integer pushAccessLevel,
                                                    Integer mergeAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);

        // 由于Gitlab不提供修改保护分支的api, 故只能先删除, 再新增
        rdmBranchRepository.unprotectBranchToGitlab(rdmRepository.getGlProjectId(), branchName);
        ProtectedBranch protectedBranch = rdmBranchRepository.protectBranchToGitlab(rdmRepository.getGlProjectId(), branchName, pushAccessLevel, mergeAccessLevel);
        return ConvertUtils.convertObject(protectedBranch, ProtectedBranchDTO.class);
    }

    @Override
    public void unprotectBranch(Long repositoryId, String branchName) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);
        rdmBranchRepository.unprotectBranchToGitlab(rdmRepository.getGlProjectId(), branchName);
    }
}
