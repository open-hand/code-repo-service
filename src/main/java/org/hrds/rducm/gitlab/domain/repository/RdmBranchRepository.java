package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedBranch;

import java.util.List;

public interface RdmBranchRepository {
    /**
     * 获取Gitlab分支
     *
     * @param projectId Gitlab项目id
     * @return
     */
    List<Branch> getBranchesFromGitlab(Integer projectId);

    /**
     * 获取Gitlab保护分支
     *
     * @param projectIdOrPath Gitlab项目id或地址
     * @return
     */
    List<ProtectedBranch> getProtectedBranchesFromGitlab(Object projectIdOrPath);

    /**
     * 设置保护分支
     *
     * @param projectIdOrPath Gitlab项目id或地址
     * @param branchName
     * @param pushAccessLevel
     * @param mergeAccessLevel
     * @return
     */
    ProtectedBranch protectBranchToGitlab(Object projectIdOrPath, String branchName, Integer pushAccessLevel, Integer mergeAccessLevel);

    /**
     * 取消保护分支
     *
     * @param projectIdOrPath Gitlab项目id或地址
     * @param branchName
     */
    void unprotectBranchToGitlab(Object projectIdOrPath, String branchName);
}
