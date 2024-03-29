package org.hrds.rducm.gitlab.api.controller.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 通用的展示用户信息的DTO
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
public class BaseC7nUserViewDTO {
    @Encrypt
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String realName;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "头像url")
    private String imageUrl;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "组织id")
    private Long organizationId;

    @ApiModelProperty(value = "是否为项目成员")
    private Boolean projectMember;
    @ApiModelProperty(value = "ladp")
    private Boolean ldap;
    @ApiModelProperty(value = "group的成员权限")
    private Integer groupAccessLevel;




    //
    // 工具方法
    // ------------------------------------------------------------------------------

    public static BaseC7nUserViewDTO convert(C7nUserVO c7nUserVO) {
        return new BaseC7nUserViewDTO()
                .setUserId(c7nUserVO.getId())
                .setRealName(c7nUserVO.getRealName())
                .setLoginName(c7nUserVO.getLoginName())
                .setImageUrl(c7nUserVO.getImageUrl())
                .setEmail(c7nUserVO.getEmail())
                .setEnabled(c7nUserVO.getEnabled())
                .setProjectMember(c7nUserVO.getProjectMember())
                .setLdap(c7nUserVO.getLdap());
    }

    public Long getUserId() {
        return userId;
    }

    public BaseC7nUserViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public BaseC7nUserViewDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public BaseC7nUserViewDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BaseC7nUserViewDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public BaseC7nUserViewDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public BaseC7nUserViewDTO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public BaseC7nUserViewDTO setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Boolean getProjectMember() {
        return projectMember;
    }

    public BaseC7nUserViewDTO setProjectMember(Boolean projectMember) {
        this.projectMember = projectMember;
        return this;
    }

    public Boolean getLdap() {
        return ldap;
    }

    public BaseC7nUserViewDTO setLdap(Boolean ldap) {
        this.ldap = ldap;
        return this;
    }

    public Integer getGroupAccessLevel() {
        return groupAccessLevel;
    }

    public void setGroupAccessLevel(Integer groupAccessLevel) {
        this.groupAccessLevel = groupAccessLevel;
    }
}
