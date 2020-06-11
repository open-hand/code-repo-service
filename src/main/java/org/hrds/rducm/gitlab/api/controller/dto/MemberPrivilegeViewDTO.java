package org.hrds.rducm.gitlab.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nProjectViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;

import java.util.Date;
import java.util.List;


public class MemberPrivilegeViewDTO {
    @ApiModelProperty("应用服务id")
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
