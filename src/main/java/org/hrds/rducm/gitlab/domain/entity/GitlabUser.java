package org.hrds.rducm.gitlab.domain.entity;

import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author xy
 */
@Table(name = "rducm_gitlab_user")
public class GitlabUser extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String initPassword;
    private Integer glUserId;
    private String glUserName;
    private Boolean glIsAdmin;
    private String glImpersonationToken;
    private Boolean isSyncGitlab;
    private Date syncDateGitlab;

    public Long getId() {
        return id;
    }

    public GitlabUser setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getInitPassword() {
        return initPassword;
    }

    public GitlabUser setInitPassword(String initPassword) {
        this.initPassword = initPassword;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public GitlabUser setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public String getGlUserName() {
        return glUserName;
    }

    public GitlabUser setGlUserName(String glUserName) {
        this.glUserName = glUserName;
        return this;
    }

    public Boolean getGlIsAdmin() {
        return glIsAdmin;
    }

    public GitlabUser setGlIsAdmin(Boolean glIsAdmin) {
        this.glIsAdmin = glIsAdmin;
        return this;
    }

    public String getGlImpersonationToken() {
        return glImpersonationToken;
    }

    public GitlabUser setGlImpersonationToken(String glImpersonationToken) {
        this.glImpersonationToken = glImpersonationToken;
        return this;
    }

    public Boolean getSyncGitlab() {
        return isSyncGitlab;
    }

    public GitlabUser setSyncGitlab(Boolean syncGitlab) {
        isSyncGitlab = syncGitlab;
        return this;
    }

    public Date getSyncDateGitlab() {
        return syncDateGitlab;
    }

    public GitlabUser setSyncDateGitlab(Date syncDateGitlab) {
        this.syncDateGitlab = syncDateGitlab;
        return this;
    }
}
