package org.hrds.rducm.gitlab.api.controller.dto;

import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

public class RdmUserViewDTO {
    @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_COMMON)
    private Long userId;

    /* gitlab用户信息 */

    private Integer glUserId;
    private String glAvatarUrl;
    private String glBio;
    private Date glCreatedAt;
    private String glName;
    private String glState;
    private String glUsername;
    private String glWebsiteUrl;
    private String glWebUrl;

    public Long getUserId() {
        return userId;
    }

    public RdmUserViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getGlUserId() {
        return glUserId;
    }

    public RdmUserViewDTO setGlUserId(Integer glUserId) {
        this.glUserId = glUserId;
        return this;
    }

    public String getGlAvatarUrl() {
        return glAvatarUrl;
    }

    public RdmUserViewDTO setGlAvatarUrl(String glAvatarUrl) {
        this.glAvatarUrl = glAvatarUrl;
        return this;
    }

    public String getGlBio() {
        return glBio;
    }

    public RdmUserViewDTO setGlBio(String glBio) {
        this.glBio = glBio;
        return this;
    }

    public Date getGlCreatedAt() {
        return glCreatedAt;
    }

    public RdmUserViewDTO setGlCreatedAt(Date glCreatedAt) {
        this.glCreatedAt = glCreatedAt;
        return this;
    }

    public String getGlName() {
        return glName;
    }

    public RdmUserViewDTO setGlName(String glName) {
        this.glName = glName;
        return this;
    }

    public String getGlState() {
        return glState;
    }

    public RdmUserViewDTO setGlState(String glState) {
        this.glState = glState;
        return this;
    }

    public String getGlUsername() {
        return glUsername;
    }

    public RdmUserViewDTO setGlUsername(String glUsername) {
        this.glUsername = glUsername;
        return this;
    }

    public String getGlWebsiteUrl() {
        return glWebsiteUrl;
    }

    public RdmUserViewDTO setGlWebsiteUrl(String glWebsiteUrl) {
        this.glWebsiteUrl = glWebsiteUrl;
        return this;
    }

    public String getGlWebUrl() {
        return glWebUrl;
    }

    public RdmUserViewDTO setGlWebUrl(String glWebUrl) {
        this.glWebUrl = glWebUrl;
        return this;
    }
}
