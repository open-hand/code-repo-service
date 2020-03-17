package org.hrds.rducm.gitlab.api.controller.dto.c7n;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
public class C7nUserDTO {
    private Long userId;

    private String loginName;

    private String email;

    private Long organizationId;

    private String realName;

    private String imageUrl;

    public Long getUserId() {
        return userId;
    }

    public C7nUserDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public C7nUserDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public C7nUserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public C7nUserDTO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public C7nUserDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public C7nUserDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
