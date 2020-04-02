package org.hrds.rducm.gitlab.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 成员权限审计日志表
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
@ApiModel("成员权限审计记录表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_member_audit_record")
public class RdmMemberAuditRecord extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_ACCESS_LEVEL = "accessLevel";
    public static final String FIELD_EXPIRES_AT = "expiresAt";
    public static final String FIELD_GL_PROJECT_ID = "glProjectId";
    public static final String FIELD_GL_USER_ID = "glUserId";
    public static final String FIELD_GL_STATE = "glState";
    public static final String FIELD_GL_ACCESS_LEVEL = "glAccessLevel";
    public static final String FIELD_GL_EXPIRES_AT = "glExpiresAt";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("主键")
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Long organizationId;
    @ApiModelProperty(value = "项目层，项目id", required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "代码仓库id", required = true)
    @NotNull
    private Long repositoryId;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "gitlab成员状态(本系统)")
    private String state;
    @ApiModelProperty(value = "gitlab成员权限级别(本系统)")
    private Integer accessLevel;
    @ApiModelProperty(value = "gitlab成员过期时间(本系统)")
    private Date expiresAt;
    @ApiModelProperty(value = "gitlab项目id", required = true)
    @NotNull
    private Integer glProjectId;
    @ApiModelProperty(value = "gitlab用户id")
    private Integer glUserId;
    @ApiModelProperty(value = "gitlab成员状态(Gitlab)")
    private String glState;
    @ApiModelProperty(value = "gitlab成员权限级别(Gitlab)")
    private Integer glAccessLevel;
    @ApiModelProperty(value = "gitlab成员过期时间(Gitlab)")
    private Date glExpiresAt;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public RdmMemberAuditRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public RdmMemberAuditRecord setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public RdmMemberAuditRecord setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmMemberAuditRecord setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RdmMemberAuditRecord setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getState() {
        return state;
    }

    public RdmMemberAuditRecord setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public RdmMemberAuditRecord setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public RdmMemberAuditRecord setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public RdmMemberAuditRecord setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public RdmMemberAuditRecord setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public String getGlState() {
        return glState;
    }

    public RdmMemberAuditRecord setGlState(String glState) {
        this.glState = glState;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public RdmMemberAuditRecord setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public RdmMemberAuditRecord setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }
}
