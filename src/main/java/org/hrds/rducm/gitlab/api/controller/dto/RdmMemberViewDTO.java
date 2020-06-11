package org.hrds.rducm.gitlab.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nProjectViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;
import java.util.List;


public class RdmMemberViewDTO {
    @ApiModelProperty("用户")
    private BaseC7nUserViewDTO user;

    @ApiModelProperty("应用服务名")
    private String repositoryName;

    @ApiModelProperty("项目角色")
    private List<String> roleNames;

    @ApiModelProperty("创建人")
    private BaseC7nUserViewDTO createdUser;

    /**
     * 成员id, 主键
     */
    @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGM)
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("项目信息")
    private BaseC7nProjectViewDTO project;

    @ApiModelProperty("代码库id")
    private Long repositoryId;

    @JsonIgnore
    private Long userId;

    @ApiModelProperty("权限")
    private Integer glAccessLevel;

    @ApiModelProperty("过期时间")
    private Date glExpiresAt;

    @ApiModelProperty("Gitlab同步标识")
    private Boolean syncGitlabFlag;

    @ApiModelProperty("Gitlab同步时间")
    private Date syncDateGitlab;

    @JsonIgnore
    private Long createdBy;

    private Date creationDate;

    private Date lastUpdateDate;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public RdmMemberViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public RdmMemberViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public RdmMemberViewDTO setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public RdmMemberViewDTO setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public RdmMemberViewDTO setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public BaseC7nUserViewDTO getUser() {
        return user;
    }

    public RdmMemberViewDTO setUser(BaseC7nUserViewDTO user) {
        this.user = user;
        return this;
    }

    public BaseC7nUserViewDTO getCreatedUser() {
        return createdUser;
    }

    public RdmMemberViewDTO setCreatedUser(BaseC7nUserViewDTO createdUser) {
        this.createdUser = createdUser;
        return this;
    }

    public BaseC7nProjectViewDTO getProject() {
        return project;
    }

    public RdmMemberViewDTO setProject(BaseC7nProjectViewDTO project) {
        this.project = project;
        return this;
    }
}

