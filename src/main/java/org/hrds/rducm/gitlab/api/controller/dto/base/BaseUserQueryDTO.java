package org.hrds.rducm.gitlab.api.controller.dto.base;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/8
 */
public class BaseUserQueryDTO {
    @ApiModelProperty(value = "用户名")
    private String realName;
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "通用参数")
    private String params;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRealName() {
        return realName;
    }

    public BaseUserQueryDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public BaseUserQueryDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }
}
