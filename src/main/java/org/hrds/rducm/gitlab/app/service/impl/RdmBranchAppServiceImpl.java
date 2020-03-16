package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.ProtectedBranchDTO;
import org.hrds.rducm.gitlab.app.service.RdmBranchAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmBranchRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmBranchService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProtectedBranchesApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabRepositoryApi;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RdmBranchAppServiceImpl implements RdmBranchAppService {
    @Autowired
    private RdmBranchRepository rdmBranchRepository;
    @Autowired
    private IRdmBranchService iRdmBranchService;
    @Autowired
    private RdmRepositoryRepository repositoryRepository;
    @Autowired
    private GitlabProtectedBranchesApi gitlabProtectedBranchesApi;

    @Override
    public List<BranchDTO> getBranches(Long repositoryId, BranchQueryDTO branchQueryDTO) {
        // 参数处理
        if (Optional.ofNullable(branchQueryDTO.getExcludeProtectedFlag()).orElse(false)) {
            return iRdmBranchService.getBranchesWithExcludeProtected(repositoryId);
        }

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

        // 校验分支是否已被保护
        ProtectedBranch glProtectedBranch = gitlabProtectedBranchesApi.getProtectedBranch(rdmRepository.getGlProjectId(), branchName);
        AssertExtensionUtils.isNull(glProtectedBranch, "error.protected.branch.exist");

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
