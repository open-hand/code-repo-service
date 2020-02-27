package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;


public class GitlabMemberViewDTO {
//    @NotNull
//    @ApiModelProperty(ApiInfoConstants.PROJECT_ID)
//    private Long projectId;

    private Long repositoryId;

    private Long userId;
//    @NotNull
//    @ApiModelProperty(ApiInfoConstants.GL_PROJECT_ID)
//    private Integer glProjectId;
//    @NotNull
//    @ApiModelProperty(ApiInfoConstants.GL_USER_ID)
//    private Integer glUserId;

    private Integer glAccessLevel;

    private Date glExpiresAt;

//    public Long getProjectId() {
//        return projectId;
//    }
//
//    public GitlabMemberCreateDTO setProjectId(Long projectId) {
//        this.projectId = projectId;
//        return this;
//    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public GitlabMemberViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabMemberViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

//    public Integer getGlProjectId() {
//        return glProjectId;
//    }
//
//    public GitlabMemberCreateDTO setGlProjectId(Integer glProjectId) {
//        this.glProjectId = glProjectId;
//        return this;
//    }
//
//    public Integer getGlUserId() {
//        return glUserId;
//    }
//
//    public GitlabMemberCreateDTO setGlUserId(Integer glUserId) {
//        this.glUserId = glUserId;
//        return this;
//    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public GitlabMemberViewDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public GitlabMemberViewDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }
}
