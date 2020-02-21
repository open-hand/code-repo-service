package org.hrds.rducm.gitlab.infra.client.gitlab.api;

import io.choerodon.core.exception.CommonException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.infra.client.gitlab.Gitlab4jClientWrapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Repository
public class GitlabPorjectApi {
    private final Gitlab4jClientWrapper gitlab4jClient;

    public GitlabPorjectApi(Gitlab4jClientWrapper gitlab4jClient) {
        this.gitlab4jClient = gitlab4jClient;
    }

    public Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .addMember(projectIdOrPath, userId, accessLevel, expiresAt);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public Member updateMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt) {
        try {
            return gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .updateMember(projectIdOrPath, userId, accessLevel, expiresAt);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public void removeMember(Integer projectId, Integer userId) {
        try {
            gitlab4jClient.getGitLabApi()
                    .getProjectApi()
                    .removeMember(projectId, userId);
        } catch (GitLabApiException e) {
            throw new CommonException(e.getMessage());
        }
    }

//    public Map<String, Object> protectedBranches(Integer projectId, String name, String mergeAccessLevel, String pushAccessLevel) {
//        try {
//            return gitlab4jClient.getGitLabApi()
//                    .getProjectApi()
//                    .protectedBranches(projectId, name, mergeAccessLevel, pushAccessLevel);
//        } catch (GitLabApiException e) {
//            throw new CommonException(e.getMessage());
//        }
//    }
//
//    public Map<String, Object> protectedBranches(Integer projectId, String name, String mergeAccessLevel, String pushAccessLevel) {
//        try {
//            return gitlab4jClient.getGitLabApi()
//                    .getOauthApi().oauthLogin().protectBranch()
//                    .deleteBranch(projectId, name, mergeAccessLevel, pushAccessLevel);
//        } catch (GitLabApiException e) {
//            throw new CommonException(e.getMessage());
//        }
//    }
}
