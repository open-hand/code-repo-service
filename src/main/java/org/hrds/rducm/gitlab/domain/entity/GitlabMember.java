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
@Table(name = "rducm_gitlab_member")
public class GitlabMember extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    private Long projectId;
    private Long repositoryId;
    private Long userId;
    private String state;
    private Integer glMemberId;
    private Integer glProjectId;
    private Integer glUserId;
    private Integer glAccessLevel;
    private Date glExpiresAt;
    private Boolean isSyncGitlab;
    private Date syncDateGitlab;

    public String getState() {
        return state;
    }

    public GitlabMember setState(String state) {
        this.state = state;
        return this;
    }

    public Long getId() {
        return id;
    }

    public GitlabMember setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public GitlabMember setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public GitlabMember setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabMember setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getGlMemberId() {
        return glMemberId;
    }

    public GitlabMember setGlMemberId(Integer glMemberId) {
        this.glMemberId = glMemberId;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public GitlabMember setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public GitlabMember setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public GitlabMember setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public GitlabMember setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Boolean getSyncGitlab() {
        return isSyncGitlab;
    }

    public GitlabMember setSyncGitlab(Boolean syncGitlab) {
        isSyncGitlab = syncGitlab;
        return this;
    }

    public Date getSyncDateGitlab() {
        return syncDateGitlab;
    }

    public GitlabMember setSyncDateGitlab(Date syncDateGitlab) {
        this.syncDateGitlab = syncDateGitlab;
        return this;
    }
}
