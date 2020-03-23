package org.hrds.rducm.gitlab.api.controller.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

/**
 * 通用的展示应用服务信息的DTO
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseC7nAppServiceViewDTO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String status;
    private Integer glProjectId;

    public Long getId() {
        return id;
    }

    public BaseC7nAppServiceViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BaseC7nAppServiceViewDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public BaseC7nAppServiceViewDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public BaseC7nAppServiceViewDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public BaseC7nAppServiceViewDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public BaseC7nAppServiceViewDTO setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }
}
