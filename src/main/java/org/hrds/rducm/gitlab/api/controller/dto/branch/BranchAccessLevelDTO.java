package org.hrds.rducm.gitlab.api.controller.dto.branch;

import io.swagger.annotations.ApiModelProperty;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.utils.JacksonJson;

public class BranchAccessLevelDTO {
    @ApiModelProperty("权限值")
    private AccessLevelDTO accessLevel;
    @ApiModelProperty("权限描述")
    private String accessLevelDescription;
    private Integer userId;
    private Integer groupId;

    public AccessLevelDTO getAccessLevel() {
        return accessLevel;
    }

    public BranchAccessLevelDTO setAccessLevel(AccessLevelDTO accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public String getAccessLevelDescription() {
        return this.accessLevelDescription;
    }

    public void setAccessLevelDescription(String accessLevelDescription) {
        this.accessLevelDescription = accessLevelDescription;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }
}
