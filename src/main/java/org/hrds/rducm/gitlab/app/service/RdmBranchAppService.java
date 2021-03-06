package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchQueryDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.ProtectedBranchDTO;

import java.util.List;

public interface RdmBranchAppService {
    /**
     * 获取分支列表
     *
     * @param projectId
     * @param repositoryId
     * @param branchQueryDTO 参数
     * @return
     */
    List<BranchDTO> getBranches(Long projectId, Long repositoryId, BranchQueryDTO branchQueryDTO);

    /**
     * 获取保护分支列表
     *
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    List<ProtectedBranchDTO> getProtectedBranches(Long projectId, Long repositoryId);

    /**
     * 设置保护分支
     *
     *
     * @param projectId
     * @param repositoryId
     * @param branchName
     * @param pushAccessLevel
     * @param mergeAccessLevel
     * @return
     */
    ProtectedBranchDTO protectBranch(Long projectId, Long repositoryId,
                                     String branchName,
                                     Integer pushAccessLevel,
                                     Integer mergeAccessLevel);

    /**
     * 更新保护分支
     *
     *
     * @param projectId
     * @param repositoryId
     * @param branchName
     * @param pushAccessLevel
     * @param mergeAccessLevel
     * @return
     */
    ProtectedBranchDTO updateProtectedBranch(Long projectId, Long repositoryId,
                                             String branchName,
                                             Integer pushAccessLevel,
                                             Integer mergeAccessLevel);

    /**
     * 取消保护分支
     *
     * @param projectId
     * @param repositoryId
     * @param branchName
     */
    void unprotectBranch(Long projectId, Long repositoryId, String branchName);
}
