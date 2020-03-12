package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RdmMemberCreateDTO {
    @NotNull
    @ApiModelProperty(value = ApiInfoConstants.USER_ID, required = true)
    private Long userId;

    @NotNull
    @ApiModelProperty(value = ApiInfoConstants.GL_ACCESS_LEVEL, required = true)
    private Integer glAccessLevel;

    @Future
    @ApiModelProperty(ApiInfoConstants.GL_EXPIRES_AT)
    private Date glExpiresAt;

    public Long getUserId() {
        return userId;
    }

    public RdmMemberCreateDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public RdmMemberCreateDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public RdmMemberCreateDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }
}
