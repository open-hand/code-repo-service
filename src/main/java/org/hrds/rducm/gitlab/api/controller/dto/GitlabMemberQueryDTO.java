package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

public class GitlabMemberQueryDTO {
    @ApiModelProperty(value = "应用服务名称")
    private String appServiceName;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "通用参数")
    private String params;

    public String getAppServiceName() {
        return appServiceName;
    }

    public GitlabMemberQueryDTO setAppServiceName(String appServiceName) {
        this.appServiceName = appServiceName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public GitlabMemberQueryDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getParams() {
        return params;
    }

    public GitlabMemberQueryDTO setParams(String params) {
        this.params = params;
        return this;
    }
}
