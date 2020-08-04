package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;


public class MemberPrivilegeViewDTO {
    @ApiModelProperty("应用服务id")
    @Encrypt
    private Long repositoryId;

    @ApiModelProperty("权限")
    private Integer accessLevel;

    public Long getRepositoryId() {
        return repositoryId;
    }

    public MemberPrivilegeViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public MemberPrivilegeViewDTO setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }
}

