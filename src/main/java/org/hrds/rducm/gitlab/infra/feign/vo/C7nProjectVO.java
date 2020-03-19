package org.hrds.rducm.gitlab.infra.feign.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

public class C7nProjectVO {
    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;

    @ApiModelProperty(value = "项目名/必填")
    private String name;

    @ApiModelProperty(value = "项目编码/必填")
    private String code;

    @ApiModelProperty(value = "组织ID/非必填")
    private Long organizationId;

    @ApiModelProperty(value = "项目图标url/非必填")
    private String imageUrl;

    @ApiModelProperty(value = "是否启用/非必填")
    private Boolean enabled;

    @ApiModelProperty(value = "项目类型code/非必填")
    private String type;

    @ApiModelProperty(value = "项目类型（遗留旧字段，一对一）:AGILE(敏捷项目),GENERAL(普通应用项目),PROGRAM(普通项目群)")
    private String category;

    @ApiModelProperty(value = "项目类型")
    private List<Long> categoryIds;

    private Long createdBy;

    private Date creationDate;

    public Long getId() {
        return id;
    }

    public C7nProjectVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public C7nProjectVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public C7nProjectVO setCode(String code) {
        this.code = code;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public C7nProjectVO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public C7nProjectVO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public C7nProjectVO setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getType() {
        return type;
    }

    public C7nProjectVO setType(String type) {
        this.type = type;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public C7nProjectVO setCategory(String category) {
        this.category = category;
        return this;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public C7nProjectVO setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
        return this;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public C7nProjectVO setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public C7nProjectVO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}