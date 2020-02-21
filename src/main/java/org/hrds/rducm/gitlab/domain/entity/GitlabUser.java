package org.hrds.rducm.gitlab.domain.entity;

import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author xy
 */
@Table(name = "rducm_gitlab_user")
public class GitlabUser extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private Long iamUserId;
    private Long userId;
    private String userName;
    private Boolean isAdmin;
    private String impersonationToken;
    private String initPassword;
    private String state;

    enum State {
        INIT,
        SUCCESS
    }

    public Long getId() {
        return id;
    }

    public GitlabUser setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getIamUserId() {
        return iamUserId;
    }

    public GitlabUser setIamUserId(Long iamUserId) {
        this.iamUserId = iamUserId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public GitlabUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public GitlabUser setAdmin(Boolean admin) {
        isAdmin = admin;
        return this;
    }

    public String getImpersonationToken() {
        return impersonationToken;
    }

    public GitlabUser setImpersonationToken(String impersonationToken) {
        this.impersonationToken = impersonationToken;
        return this;
    }

    public String getInitPassword() {
        return initPassword;
    }

    public GitlabUser setInitPassword(String initPassword) {
        this.initPassword = initPassword;
        return this;
    }

    public String getState() {
        return state;
    }

    public GitlabUser setState(String state) {
        this.state = state;
        return this;
    }
}
