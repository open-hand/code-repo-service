package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nProjectViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * 成员权限审计日志表
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
@ApiModel("成员权限审计记录表")
public class RdmMemberAuditRecordViewDTO {
    @ApiModelProperty("主键")
    @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMAR)
    private Long id;

    @ApiModelProperty(value = "组织id", required = true)
    private Long organizationId;

    @ApiModelProperty(value = "项目层，项目id", required = true)
    private Long projectId;

    @ApiModelProperty("项目信息")
    private BaseC7nProjectViewDTO project;

    @ApiModelProperty(value = "代码仓库id", required = true)
    private Long repositoryId;

    @ApiModelProperty(value = "代码仓库名称")
    private String repositoryName;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户信息")
    private BaseC7nUserViewDTO user;

    @ApiModelProperty(value = "gitlab成员状态(本系统)")
    private String state;

    @ApiModelProperty(value = "gitlab成员权限级别(本系统)")
    private Integer accessLevel;

    @ApiModelProperty(value = "gitlab成员过期时间(本系统)")
    private Date expiresAt;

    @ApiModelProperty(value = "gitlab项目id", required = true)
    private Integer glProjectId;

    @ApiModelProperty(value = "gitlab用户id")
    private Integer glUserId;

    @ApiModelProperty(value = "gitlab成员状态(Gitlab)")
    private String glState;

    @ApiModelProperty(value = "gitlab成员权限级别(Gitlab)")
    private Integer glAccessLevel;

    @ApiModelProperty(value = "gitlab成员过期时间(Gitlab)")
    private Date glExpiresAt;

    @ApiModelProperty(value = "同步标识")
    private Boolean syncFlag;

    @ApiModelProperty(value = "权限级别是否一致")
    private Boolean accessLevelSyncFlag;

    @ApiModelProperty(value = "过期日期是否一致")
    private Boolean expiresAtSyncFlag;

    public Long getId() {
        return id;
    }

    public RdmMemberAuditRecordViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public RdmMemberAuditRecordViewDTO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public RdmMemberAuditRecordViewDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmMemberAuditRecordViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public RdmMemberAuditRecordViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RdmMemberAuditRecordViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public BaseC7nUserViewDTO getUser() {
        return user;
    }

    public RdmMemberAuditRecordViewDTO setUser(BaseC7nUserViewDTO user) {
        this.user = user;
        return this;
    }

    public String getState() {
        return state;
    }

    public RdmMemberAuditRecordViewDTO setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public RdmMemberAuditRecordViewDTO setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public RdmMemberAuditRecordViewDTO setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public RdmMemberAuditRecordViewDTO setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public RdmMemberAuditRecordViewDTO setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public String getGlState() {
        return glState;
    }

    public RdmMemberAuditRecordViewDTO setGlState(String glState) {
        this.glState = glState;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public RdmMemberAuditRecordViewDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public RdmMemberAuditRecordViewDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Boolean getAccessLevelSyncFlag() {
        return accessLevelSyncFlag;
    }

    public RdmMemberAuditRecordViewDTO setAccessLevelSyncFlag(Boolean accessLevelSyncFlag) {
        this.accessLevelSyncFlag = accessLevelSyncFlag;
        return this;
    }

    public Boolean getExpiresAtSyncFlag() {
        return expiresAtSyncFlag;
    }

    public RdmMemberAuditRecordViewDTO setExpiresAtSyncFlag(Boolean expiresAtSyncFlag) {
        this.expiresAtSyncFlag = expiresAtSyncFlag;
        return this;
    }

    public Boolean getSyncFlag() {
        return syncFlag;
    }

    public RdmMemberAuditRecordViewDTO setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
        return this;
    }

    public BaseC7nProjectViewDTO getProject() {
        return project;
    }

    public RdmMemberAuditRecordViewDTO setProject(BaseC7nProjectViewDTO project) {
        this.project = project;
        return this;
    }
}
