package org.hrds.rducm.gitlab.api.controller.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;

import java.util.Date;
import java.util.List;

/**
 * 通用的展示项目信息的DTO
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
public class BaseC7nProjectViewDTO {
    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "项目名")
    private String projectName;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "组织ID")
    private Long organizationId;

    @ApiModelProperty(value = "项目图标url")
    private String imageUrl;

    @ApiModelProperty(value = "是否启用")
    @JsonIgnore
    private Boolean enabled;

    @ApiModelProperty(value = "项目类型code")
    @JsonIgnore
    private String type;

    @ApiModelProperty(value = "项目类型（遗留旧字段，一对一）:AGILE(敏捷项目),GENERAL(普通应用项目),PROGRAM(普通项目群)")
    @JsonIgnore
    private String category;

    //
    // 工具方法
    // ------------------------------------------------------------------------------

    public static BaseC7nProjectViewDTO convert(C7nProjectVO c7nProjectVO) {
        return new BaseC7nProjectViewDTO()
                .setProjectId(c7nProjectVO.getId())
                .setProjectName(c7nProjectVO.getName())
                .setProjectCode(c7nProjectVO.getCode())
                .setImageUrl(c7nProjectVO.getImageUrl())
                .setOrganizationId(c7nProjectVO.getOrganizationId())
                .setEnabled(c7nProjectVO.getEnabled())
                .setType(c7nProjectVO.getType())
                .setCategory(c7nProjectVO.getCategory());
    }

    public Long getProjectId() {
        return projectId;
    }

    public BaseC7nProjectViewDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public BaseC7nProjectViewDTO setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public BaseC7nProjectViewDTO setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public BaseC7nProjectViewDTO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BaseC7nProjectViewDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public BaseC7nProjectViewDTO setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getType() {
        return type;
    }

    public BaseC7nProjectViewDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public BaseC7nProjectViewDTO setCategory(String category) {
        this.category = category;
        return this;
    }
}
