package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;


public class GitlabMemberCreateDTO {
//    @NotNull
//    @ApiModelProperty(ApiInfoConstants.PROJECT_ID)
//    private Long projectId;
    @NotNull
    @ApiModelProperty(ApiInfoConstants.REPOSITORY_ID)
    private Long repositoryId;
    @NotNull
    @ApiModelProperty(ApiInfoConstants.USER_ID)
    private Long userId;
//    @NotNull
//    @ApiModelProperty(ApiInfoConstants.GL_PROJECT_ID)
//    private Integer glProjectId;
//    @NotNull
//    @ApiModelProperty(ApiInfoConstants.GL_USER_ID)
//    private Integer glUserId;
    @NotNull
    @ApiModelProperty(ApiInfoConstants.GL_ACCESS_LEVEL)
    private Integer glAccessLevel;
    @Future
    @ApiModelProperty(ApiInfoConstants.GL_EXPIRES_AT)
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

    public GitlabMemberCreateDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabMemberCreateDTO setUserId(Long userId) {
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

    public GitlabMemberCreateDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public GitlabMemberCreateDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }
}
