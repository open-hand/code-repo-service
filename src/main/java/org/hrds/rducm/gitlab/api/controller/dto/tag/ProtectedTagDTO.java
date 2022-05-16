package org.hrds.rducm.gitlab.api.controller.dto.tag;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.utils.JacksonJson;

public class ProtectedTagDTO {

    public static class CreateAccessLevelDTO {
        @ApiModelProperty("权限值")
        private AccessLevel accessLevel;
        @ApiModelProperty("权限描述")
        private String accessLevelDescription;

        public AccessLevel getAccessLevel() {
            return accessLevel;
        }

        public void setAccessLevel(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
        }

        public String getAccessLevelDescription() {
            return accessLevelDescription;
        }

        public void setAccessLevelDescription(String accessLevelDescription) {
            this.accessLevelDescription = accessLevelDescription;
        }
    }

    @ApiModelProperty("标签名")
    private String name;
    @ApiModelProperty("是否允许创建-权限级别")
    private List<CreateAccessLevelDTO> createAccessLevels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CreateAccessLevelDTO> getCreateAccessLevels() {
        return createAccessLevels;
    }

    public void setCreateAccessLevels(List<CreateAccessLevelDTO> createAccessLevels) {
        this.createAccessLevels = createAccessLevels;
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }
}
