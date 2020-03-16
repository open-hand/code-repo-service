package org.hrds.rducm.gitlab.infra.feign.vo;

import java.util.Date;

public class UserDTO {

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

    public Long getId() {
        return id;
    }

    public UserDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public UserDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public UserDTO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public UserDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public UserDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public UserDTO setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
        return this;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public UserDTO setEnabled(Boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public Boolean getLdap() {
        return ldap;
    }

    public UserDTO setLdap(Boolean ldap) {
        this.ldap = ldap;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public UserDTO setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public UserDTO setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Date getLastPasswordUpdatedAt() {
        return lastPasswordUpdatedAt;
    }

    public UserDTO setLastPasswordUpdatedAt(Date lastPasswordUpdatedAt) {
        this.lastPasswordUpdatedAt = lastPasswordUpdatedAt;
        return this;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public UserDTO setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public UserDTO setLocked(Boolean locked) {
        isLocked = locked;
        return this;
    }

    public Date getLockedUntilAt() {
        return lockedUntilAt;
    }

    public UserDTO setLockedUntilAt(Date lockedUntilAt) {
        this.lockedUntilAt = lockedUntilAt;
        return this;
    }

    public Integer getPasswordAttempt() {
        return passwordAttempt;
    }

    public UserDTO setPasswordAttempt(Integer passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
        return this;
    }
}