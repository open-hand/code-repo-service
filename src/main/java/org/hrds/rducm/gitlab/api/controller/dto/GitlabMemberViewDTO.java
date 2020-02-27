package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;


public class GitlabMemberViewDTO {
    private String realName;

    private String loginName;

    private String appServiceName;

    private String projectRoleName;

    private String createdByName;

    /**
     * 成员id, 主键
     */
    private Long id;

    private Long projectId;

    private Long repositoryId;

    private Long userId;

    private Integer glAccessLevel;

    private Date glExpiresAt;

    private Long createdBy;

    private Date creationDate;

    public Long getId() {
        return id;
    }

    public GitlabMemberViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public GitlabMemberViewDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public GitlabMemberViewDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getAppServiceName() {
        return appServiceName;
    }

    public GitlabMemberViewDTO setAppServiceName(String appServiceName) {
        this.appServiceName = appServiceName;
        return this;
    }

    public String getProjectRoleName() {
        return projectRoleName;
    }

    public GitlabMemberViewDTO setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
        return this;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public GitlabMemberViewDTO setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public GitlabMemberViewDTO setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public GitlabMemberViewDTO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

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

    public Long getProjectId() {
        return projectId;
    }

    public GitlabMemberViewDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }
}

