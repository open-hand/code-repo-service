package org.hrds.rducm.gitlab.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/4/7
 */
public class MemberAuthDetailViewDTO {
    @JsonIgnore
    private Long userId;
    private BaseC7nUserViewDTO user;
    @ApiModelProperty(value = "已授权服务数")
    private Integer authorizedRepositoryCount;

    @ApiModelProperty(value = "总服务数")
    private Integer allRepositoryCount;

    @ApiModelProperty(value = "已授权服务占比")
    private BigDecimal authorizedRepositoryPercent;

    public Long getUserId() {
        return userId;
    }

    public MemberAuthDetailViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public BaseC7nUserViewDTO getUser() {
        return user;
    }

    public MemberAuthDetailViewDTO setUser(BaseC7nUserViewDTO user) {
        this.user = user;
        return this;
    }

    public Integer getAuthorizedRepositoryCount() {
        return authorizedRepositoryCount;
    }

    public MemberAuthDetailViewDTO setAuthorizedRepositoryCount(Integer authorizedRepositoryCount) {
        this.authorizedRepositoryCount = authorizedRepositoryCount;
        return this;
    }

    public Integer getAllRepositoryCount() {
        return allRepositoryCount;
    }

    public MemberAuthDetailViewDTO setAllRepositoryCount(Integer allRepositoryCount) {
        this.allRepositoryCount = allRepositoryCount;
        return this;
    }

    public BigDecimal getAuthorizedRepositoryPercent() {
        return authorizedRepositoryPercent;
    }

    public MemberAuthDetailViewDTO setAuthorizedRepositoryPercent(BigDecimal authorizedRepositoryPercent) {
        this.authorizedRepositoryPercent = authorizedRepositoryPercent;
        return this;
    }
}