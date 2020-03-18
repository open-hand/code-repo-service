package org.hrds.rducm.gitlab.infra.feign.vo;

import io.swagger.annotations.ApiModelProperty;

public class C7nRoleVO {
    private Long id;

    @ApiModelProperty(value = "角色名/必填")
    private String name;

    @ApiModelProperty(value = "角色编码/必填")
    private String code;

    @ApiModelProperty(value = "角色描述/非必填")
    private String description;

    @ApiModelProperty(value = "角色层级/必填")
    private String resourceLevel;


    public Long getId() {
        return id;
    }

    public C7nRoleVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public C7nRoleVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public C7nRoleVO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public C7nRoleVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getResourceLevel() {
        return resourceLevel;
    }

    public C7nRoleVO setResourceLevel(String resourceLevel) {
        this.resourceLevel = resourceLevel;
        return this;
    }
}