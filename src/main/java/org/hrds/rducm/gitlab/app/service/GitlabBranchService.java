package org.hrds.rducm.gitlab.app.service;

import org.gitlab4j.api.models.ProtectedBranch;

import java.util.List;

public interface GitlabBranchService {
    List<ProtectedBranch> getProtectedBranches(Integer glProjectId);

    ProtectedBranch protectBranch(Integer glProjectId,
                                  String branchName,
                                  Integer pushAccessLevel,
                                  Integer mergeAccessLevel);

    void unprotectBranch(Integer glProjectId, String branchName);
}
