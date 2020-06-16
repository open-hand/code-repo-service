package org.hrds.rducm.gitlab.infra.feign.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/15
 */
public class C7nTenantVO {
    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("租户编号")
    private String tenantNum;

    @ApiModelProperty("是否启用")
    private Integer enabledFlag;

    @ApiModelProperty("限制用户数")
    private Integer limitUserQty;

    public Long getTenantId() {
        return tenantId;
    }

    public C7nTenantVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public C7nTenantVO setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public C7nTenantVO setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public C7nTenantVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Integer getLimitUserQty() {
        return limitUserQty;
    }

    public C7nTenantVO setLimitUserQty(Integer limitUserQty) {
        this.limitUserQty = limitUserQty;
        return this;
    }
}
