package org.hrds.rducm.gitlab.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@ModifyAudit
@VersionAudit
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_member_sync_audit_log")
public class RdmMemberSyncAuditLog extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private Long repositoryId;
    private Long userId;
    private String state;
    private Integer accessLevel;
    private Date expiresAt;

    private Integer glProjectId;
    private Integer glUserId;
    private String glState;
    private Integer glAccessLevel;
    private Date glExpiresAt;

    public Long getId() {
        return id;
    }

    public RdmMemberSyncAuditLog setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmMemberSyncAuditLog setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RdmMemberSyncAuditLog setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getState() {
        return state;
    }

    public RdmMemberSyncAuditLog setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public RdmMemberSyncAuditLog setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public RdmMemberSyncAuditLog setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public RdmMemberSyncAuditLog setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public RdmMemberSyncAuditLog setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public String getGlState() {
        return glState;
    }

    public RdmMemberSyncAuditLog setGlState(String glState) {
        this.glState = glState;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public RdmMemberSyncAuditLog setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public RdmMemberSyncAuditLog setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }
}
