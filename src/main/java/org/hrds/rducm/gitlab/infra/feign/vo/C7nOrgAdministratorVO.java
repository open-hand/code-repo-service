package org.hrds.rducm.gitlab.infra.feign.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author jiameng.cao
 */
public class C7nOrgAdministratorVO {
    @ApiModelProperty(value = "主键，用户ID")
    private Long id;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "登录名")
    private String loginName;
    @ApiModelProperty(value = "状态，是否启用")
    private Boolean enabled;
    @ApiModelProperty(value = "安全状态，锁定")
    private Boolean locked;
    @ApiModelProperty(value = "是否为外部用户")
    private Boolean externalUser;
    @ApiModelProperty(value = "用户被分配组织管理员角色的创建时间")
    private Date creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public C7nOrgAdministratorVO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getExternalUser() {
        return externalUser;
    }

    public void setExternalUser(Boolean externalUser) {
        this.externalUser = externalUser;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
