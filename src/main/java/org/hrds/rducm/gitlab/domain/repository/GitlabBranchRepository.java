package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.infra.enums.GitlabAccessLevel;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Date;
import java.util.List;

public interface GitlabBranchRepository {
    List<Branch> getBranchesFromGitlab(Integer projectId);

    List<ProtectedBranch> getProtectedBranchesFromGitlab(Object projectIdOrPath);

    ProtectedBranch protectBranchToGitlab(Object projectIdOrPath, String branchName, Integer pushAccessLevel, Integer mergeAccessLevel);

    void unprotectBranchToGitlab(Object projectIdOrPath, String branchName);
}
