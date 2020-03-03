package org.hrds.rducm.gitlab.app.service;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;

import java.util.List;

public interface GitlabBranchService {
    List<Branch> getBranches(Long repositoryId);

    List<ProtectedBranch> getProtectedBranches(Long repositoryId);

    ProtectedBranch protectBranch(Long repositoryId,
                                  String branchName,
                                  Integer pushAccessLevel,
                                  Integer mergeAccessLevel);

    void unprotectBranch(Long repositoryId, String branchName);
}
