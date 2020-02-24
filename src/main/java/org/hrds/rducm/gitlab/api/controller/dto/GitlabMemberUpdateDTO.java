package org.hrds.rducm.gitlab.api.controller.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class GitlabMemberUpdateDTO {
    @NotNull
    private Integer glProjectId;
    @NotNull
    private Integer glUserId;
    @NotNull
    private Integer glAccessLevel;
    @Future
    private Date glExpiresAt;
    @NotNull
    private Long objectVersionNumber;

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public GitlabMemberUpdateDTO setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public GitlabMemberUpdateDTO setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public GitlabMemberUpdateDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public GitlabMemberUpdateDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public GitlabMemberUpdateDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
