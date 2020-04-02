package org.hrds.rducm.gitlab.infra.feign.vo;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/2
 */
public class C7nGlUserVO {
    private Long iamUserId;

    private Long gitlabUserId;

    private String gitlabToken;

    private String gitlabUserName;

    private Boolean isGitlabAdmin;

    public Long getIamUserId() {
        return iamUserId;
    }

    public C7nGlUserVO setIamUserId(Long iamUserId) {
        this.iamUserId = iamUserId;
        return this;
    }

    public Long getGitlabUserId() {
        return gitlabUserId;
    }

    public C7nGlUserVO setGitlabUserId(Long gitlabUserId) {
        this.gitlabUserId = gitlabUserId;
        return this;
    }

    public String getGitlabToken() {
        return gitlabToken;
    }

    public C7nGlUserVO setGitlabToken(String gitlabToken) {
        this.gitlabToken = gitlabToken;
        return this;
    }

    public String getGitlabUserName() {
        return gitlabUserName;
    }

    public C7nGlUserVO setGitlabUserName(String gitlabUserName) {
        this.gitlabUserName = gitlabUserName;
        return this;
    }

    public Boolean getGitlabAdmin() {
        return isGitlabAdmin;
    }

    public C7nGlUserVO setGitlabAdmin(Boolean gitlabAdmin) {
        isGitlabAdmin = gitlabAdmin;
        return this;
    }
}
