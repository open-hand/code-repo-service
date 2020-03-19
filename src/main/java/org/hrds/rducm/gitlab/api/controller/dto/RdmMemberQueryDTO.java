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
    private String realName;
    @ApiModelProperty(value = "登录名")
    private String loginName;
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

    public String getRealName() {
        return realName;
    }

    public RdmMemberQueryDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public RdmMemberQueryDTO setLoginName(String loginName) {
        this.loginName = loginName;
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
