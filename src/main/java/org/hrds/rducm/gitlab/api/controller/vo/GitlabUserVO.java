package org.hrds.rducm.gitlab.api.controller.vo;

public class GitlabUserVO {
    private Long id;
    private Long userId;
    private String userName;
    private Boolean isAdmin;
    private String impersonationToken;
    private String initPassword;

    public Long getId() {
        return id;
    }

    public GitlabUserVO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabUserVO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public GitlabUserVO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public GitlabUserVO setAdmin(Boolean admin) {
        isAdmin = admin;
        return this;
    }

    public String getImpersonationToken() {
        return impersonationToken;
    }

    public GitlabUserVO setImpersonationToken(String impersonationToken) {
        this.impersonationToken = impersonationToken;
        return this;
    }

    public String getInitPassword() {
        return initPassword;
    }

    public GitlabUserVO setInitPassword(String initPassword) {
        this.initPassword = initPassword;
        return this;
    }

    @Override
    public String toString() {
        return "GitlabUserVO{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", isAdmin=" + isAdmin +
                ", impersonationToken='" + impersonationToken + '\'' +
                ", initPassword='" + initPassword + '\'' +
                '}';
    }
}
