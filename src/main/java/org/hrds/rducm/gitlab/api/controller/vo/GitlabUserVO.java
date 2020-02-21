package org.hrds.rducm.gitlab.api.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.gitlab4j.api.models.User;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitlabUserVO {
    private Long id;
    private Long userId;
    private String initPassword;
    private Integer glUserId;
    private Boolean isSyncGitlab;
    private Date syncDateGitlab;
    private User glUser;

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

    public String getInitPassword() {
        return initPassword;
    }

    public GitlabUserVO setInitPassword(String initPassword) {
        this.initPassword = initPassword;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public GitlabUserVO setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public Boolean getSyncGitlab() {
        return isSyncGitlab;
    }

    public GitlabUserVO setSyncGitlab(Boolean syncGitlab) {
        isSyncGitlab = syncGitlab;
        return this;
    }

    public Date getSyncDateGitlab() {
        return syncDateGitlab;
    }

    public GitlabUserVO setSyncDateGitlab(Date syncDateGitlab) {
        this.syncDateGitlab = syncDateGitlab;
        return this;
    }

    public User getGlUser() {
        return glUser;
    }

    public GitlabUserVO setGlUser(User glUser) {
        this.glUser = glUser;
        return this;
    }
}
