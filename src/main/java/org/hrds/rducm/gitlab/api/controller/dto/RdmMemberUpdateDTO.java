package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RdmMemberUpdateDTO {
    @NotNull
    @ApiModelProperty(ApiInfoConstants.GL_ACCESS_LEVEL)
    private Integer glAccessLevel;
    @Future
    @ApiModelProperty(ApiInfoConstants.GL_EXPIRES_AT)
    private Date glExpiresAt;
    @NotNull
    @ApiModelProperty(ApiInfoConstants.OBJECT_VERSION_NUMBER)
    private Long objectVersionNumber;

    public Integer getGlAccessLevel() {
        return glAccessLevel;
    }

    public RdmMemberUpdateDTO setGlAccessLevel(Integer glAccessLevel) {
        this.glAccessLevel = glAccessLevel;
        return this;
    }

    public Date getGlExpiresAt() {
        return glExpiresAt;
    }

    public RdmMemberUpdateDTO setGlExpiresAt(Date glExpiresAt) {
        this.glExpiresAt = glExpiresAt;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public RdmMemberUpdateDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
