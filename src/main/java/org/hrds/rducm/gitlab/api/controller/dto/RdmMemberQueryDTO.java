package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class RdmMemberQueryDTO {
    @ApiModelProperty(value = "应用服务id")
    private List<Long> repositoryIds;
    @ApiModelProperty(value = "应用服务名称")
    private String appServiceName;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "通用参数")
    private String params;

    public List<Long> getRepositoryIds() {
        return repositoryIds;
    }

    public RdmMemberQueryDTO setRepositoryIds(List<Long> repositoryIds) {
        this.repositoryIds = repositoryIds;
        return this;
    }

    public String getAppServiceName() {
        return appServiceName;
    }

    public RdmMemberQueryDTO setAppServiceName(String appServiceName) {
        this.appServiceName = appServiceName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public RdmMemberQueryDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getParams() {
        return params;
    }

    public RdmMemberQueryDTO setParams(String params) {
        this.params = params;
        return this;
    }
}
