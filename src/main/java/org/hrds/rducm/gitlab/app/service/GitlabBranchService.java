package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.branch.BranchDTO;
import org.hrds.rducm.gitlab.api.controller.dto.branch.ProtectedBranchDTO;

import java.util.List;

public interface GitlabBranchService {
    List<BranchDTO> getBranches(Long repositoryId);

    List<ProtectedBranchDTO> getProtectedBranches(Long repositoryId);

    ProtectedBranchDTO protectBranch(Long repositoryId,
                                     String branchName,
                                     Integer pushAccessLevel,
                                     Integer mergeAccessLevel);

    ProtectedBranchDTO updateProtectedBranch(Long repositoryId,
                                             String branchName,
                                             Integer pushAccessLevel,
                                             Integer mergeAccessLevel);

    void unprotectBranch(Long repositoryId, String branchName);
}
