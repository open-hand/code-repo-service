package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Set;


public class RepositoryPrivilegeViewDTO {
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("应用服务id")
    private Set<Long> appServiceIds;

    public Long getUserId() {
        return userId;
    }

    public RepositoryPrivilegeViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Set<Long> getAppServiceIds() {
        return appServiceIds;
    }

    public RepositoryPrivilegeViewDTO setAppServiceIds(Set<Long> appServiceIds) {
        this.appServiceIds = appServiceIds;
        return this;
    }
}

