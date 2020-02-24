package org.hrds.rducm.gitlab.api.controller.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class GitlabMemberDTO {
    @NotNull
    private Long projectId;
    @NotNull
    private Long repositoryId;
    @NotNull
    private Long userId;
    @NotNull
    private Integer glProjectId;
    @NotNull
    private Integer glUserId;
    @NotNull
    private Integer glAccessLevel;
    @Future
    private Date glExpiresAt;

    public Long getProjectId() {
        return projectId;
    }

    public GitlabMemberDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public GitlabMemberDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabMemberDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public GitlabMemberDTO setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public GitlabMemberDTO setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public GitlabMemberDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public GitlabMemberDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }
}
