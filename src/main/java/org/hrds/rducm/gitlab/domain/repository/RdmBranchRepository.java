package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;

import java.util.List;

public interface RdmBranchRepository {
    List<Branch> getBranchesFromGitlab(Integer projectId);

    List<ProtectedBranch> getProtectedBranchesFromGitlab(Object projectIdOrPath);

    ProtectedBranch protectBranchToGitlab(Object projectIdOrPath, String branchName, Integer pushAccessLevel, Integer mergeAccessLevel);

    void unprotectBranchToGitlab(Object projectIdOrPath, String branchName);
}
