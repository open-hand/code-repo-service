package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class GitlabMemberUpdateDTO {
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
    @NotNull
    @ApiModelProperty(ApiInfoConstants.OBJECT_VERSION_NUMBER)
    private Long objectVersionNumber;

//    public Integer getGlProjectId() {
//        return glProjectId;
//    }
//
//    public GitlabMemberUpdateDTO setGlProjectId(Integer glProjectId) {
//        this.glProjectId = glProjectId;
//        return this;
//    }
//
//    public Integer getGlUserId() {
//        return glUserId;
//    }
//
//    public GitlabMemberUpdateDTO setGlUserId(Integer glUserId) {
//        this.glUserId = glUserId;
//        return this;
//    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public GitlabMemberUpdateDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public GitlabMemberUpdateDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public GitlabMemberUpdateDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
