package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Set;

@ApiModel
public class RdmMemberQueryDTO {
    /**
     * 组织层
     */
    @ApiModelProperty(value = "项目id")
    private Set<Long> projectIds;
    /**
     * 项目层
     */
    @ApiModelProperty(value = "应用服务id")
    private Set<Long> repositoryIds;

    @ApiModelProperty(value = "应用服务名称")
    private String appServiceName;
    @ApiModelProperty(value = "用户名")
    private String realName;
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "通用参数")
    private String params;

    public Set<Long> getProjectIds() {
        return projectIds;
    }

    public RdmMemberQueryDTO setProjectIds(Set<Long> projectIds) {
        this.projectIds = projectIds;
        return this;
    }

    public Set<Long> getRepositoryIds() {
        return repositoryIds;
    }

    public RdmMemberQueryDTO setRepositoryIds(Set<Long> repositoryIds) {
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
