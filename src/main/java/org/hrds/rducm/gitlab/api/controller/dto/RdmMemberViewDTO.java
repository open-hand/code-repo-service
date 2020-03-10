package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


public class RdmMemberViewDTO {
    @ApiModelProperty("用户名")
    private String realName;

    @ApiModelProperty("登录名")
    private String loginName;

    @ApiModelProperty("应用服务名")
    private String appServiceName;

    @ApiModelProperty("项目角色")
    private String projectRoleName;

    @ApiModelProperty("创建人")
    private String createdByName;

    /**
     * 成员id, 主键
     */
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("代码库id")
    private Long repositoryId;

    private Long userId;

    @ApiModelProperty("权限")
    private Integer glAccessLevel;

    @ApiModelProperty("过期时间")
    private Date glExpiresAt;

    @ApiModelProperty("Gitlab同步标识")
    private Boolean syncGitlabFlag;

    @ApiModelProperty("Gitlab同步时间")
    private Date syncDateGitlab;

    private Long createdBy;

    private Date creationDate;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public RdmMemberViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public RdmMemberViewDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public RdmMemberViewDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getAppServiceName() {
        return appServiceName;
    }

    public RdmMemberViewDTO setAppServiceName(String appServiceName) {
        this.appServiceName = appServiceName;
        return this;
    }

    public String getProjectRoleName() {
        return projectRoleName;
    }

    public RdmMemberViewDTO setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public RdmMemberViewDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public RdmMemberViewDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public RdmMemberViewDTO setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public RdmMemberViewDTO setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public RdmMemberViewDTO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmMemberViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RdmMemberViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public RdmMemberViewDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public RdmMemberViewDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public Boolean getSyncGitlabFlag() {
        return syncGitlabFlag;
    }

    public RdmMemberViewDTO setSyncGitlabFlag(Boolean syncGitlabFlag) {
        this.syncGitlabFlag = syncGitlabFlag;
        return this;
    }

    public Date getSyncDateGitlab() {
        return syncDateGitlab;
    }

    public RdmMemberViewDTO setSyncDateGitlab(Date syncDateGitlab) {
        this.syncDateGitlab = syncDateGitlab;
        return this;
    }
}

