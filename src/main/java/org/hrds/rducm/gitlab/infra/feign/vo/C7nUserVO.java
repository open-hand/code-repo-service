package org.hrds.rducm.gitlab.infra.feign.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

public class C7nUserVO {

    private Long id;

    private String loginName;

    private String email;

    private Long organizationId;

    private String password;

    private String realName;

    private String phone;

    private String imageUrl;

    private String profilePhoto;

    private Boolean isEnabled;

    private Boolean ldap;

    private String language;

    private String timeZone;

    private Date lastPasswordUpdatedAt;

    private Date lastLoginAt;

    private Boolean isLocked;

    private Date lockedUntilAt;

    private Integer passwordAttempt;

    @ApiModelProperty("用户对应的gitlab用户id")
    private Long gitlabUserId;

    private List<C7nRoleVO> roles;

    public Long getId() {
        return id;
    }

    public C7nUserVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public C7nUserVO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public C7nUserVO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public C7nUserVO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public C7nUserVO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public C7nUserVO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public C7nUserVO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public C7nUserVO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public C7nUserVO setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
        return this;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public C7nUserVO setEnabled(Boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public Boolean getLdap() {
        return ldap;
    }

    public C7nUserVO setLdap(Boolean ldap) {
        this.ldap = ldap;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public C7nUserVO setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public C7nUserVO setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Date getLastPasswordUpdatedAt() {
        return lastPasswordUpdatedAt;
    }

    public C7nUserVO setLastPasswordUpdatedAt(Date lastPasswordUpdatedAt) {
        this.lastPasswordUpdatedAt = lastPasswordUpdatedAt;
        return this;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public C7nUserVO setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public C7nUserVO setLocked(Boolean locked) {
        isLocked = locked;
        return this;
    }

    public Date getLockedUntilAt() {
        return lockedUntilAt;
    }

    public C7nUserVO setLockedUntilAt(Date lockedUntilAt) {
        this.lockedUntilAt = lockedUntilAt;
        return this;
    }

    public Integer getPasswordAttempt() {
        return passwordAttempt;
    }

    public C7nUserVO setPasswordAttempt(Integer passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
        return this;
    }

    public Long getGitlabUserId() {
        return gitlabUserId;
    }

    public C7nUserVO setGitlabUserId(Long gitlabUserId) {
        this.gitlabUserId = gitlabUserId;
        return this;
    }

    public List<C7nRoleVO> getRoles() {
        return roles;
    }

    public C7nUserVO setRoles(List<C7nRoleVO> roles) {
        this.roles = roles;
        return this;
    }
}