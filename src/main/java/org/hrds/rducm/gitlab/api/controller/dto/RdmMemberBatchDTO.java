package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


public class RdmMemberBatchDTO {
    @NotEmpty
    @ApiModelProperty(value = ApiInfoConstants.REPOSITORY_ID, dataType = "Long", required = true)
    @Encrypt
    private List<Long> repositoryIds;

    @ApiModelProperty("基于何种角色添加")
    private Boolean baseRole = false;

    @ApiModelProperty(value = ApiInfoConstants.GL_ACCESS_LEVEL, required = true)
    private Integer glAccessLevel;

    @ApiModelProperty(ApiInfoConstants.GL_EXPIRES_AT)
    private Date glExpiresAt;


    @NotEmpty
    @Valid
    @ApiModelProperty("新增成员信息")
    private List<GitlabMemberCreateDTO> members;

    public static class GitlabMemberCreateDTO {
        @NotNull
        @ApiModelProperty(value = ApiInfoConstants.USER_ID, required = true)
        @Encrypt
        private Long userId;
        @NotNull
        @ApiModelProperty(value = ApiInfoConstants.GL_ACCESS_LEVEL, required = true)
        private Integer glAccessLevel;
        @Future
        @ApiModelProperty(ApiInfoConstants.GL_EXPIRES_AT)
        private Date glExpiresAt;

        private Integer gUserId;

        private Integer gGroupId;

        public Integer getgGroupId() {
            return gGroupId;
        }

        public void setgGroupId(Integer gGroupId) {
            this.gGroupId = gGroupId;
        }

        public Integer getgUserId() {
            return gUserId;
        }

        public void setgUserId(Integer gUserId) {
            this.gUserId = gUserId;
        }

        public Long getUserId() {
            return userId;
        }

        public GitlabMemberCreateDTO setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

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

    public List<Long> getRepositoryIds() {
        return repositoryIds;
    }

    public RdmMemberBatchDTO setRepositoryIds(List<Long> repositoryIds) {
        this.repositoryIds = repositoryIds;
        return this;
    }

    public List<GitlabMemberCreateDTO> getMembers() {
        return members;
    }

    public RdmMemberBatchDTO setMembers(List<GitlabMemberCreateDTO> members) {
        this.members = members;
        return this;
    }


    public Boolean getBaseRole() {
        return baseRole;
    }

    public void setBaseRole(Boolean baseRole) {
        this.baseRole = baseRole;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public void setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public void setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
    }
}
