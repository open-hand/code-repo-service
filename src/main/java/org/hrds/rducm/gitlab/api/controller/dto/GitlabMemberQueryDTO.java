package org.hrds.rducm.gitlab.api.controller.dto;

public class GitlabMemberQueryDTO {
    private String appServiceName;
    private String username;
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
