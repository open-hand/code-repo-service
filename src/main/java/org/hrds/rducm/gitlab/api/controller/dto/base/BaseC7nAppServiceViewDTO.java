package org.hrds.rducm.gitlab.api.controller.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Optional;

/**
 * 通用的展示应用服务信息的DTO
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseC7nAppServiceViewDTO {
    @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_COMMON)
    private Long repositoryId;
    private String repositoryName;
    private String repositoryCode;
    private String type;
    @ApiModelProperty("应用服务图标url")
    private String imgUrl;
    private Integer glProjectId;

    //
    // 工具方法
    // ------------------------------------------------------------------------------

    public static BaseC7nAppServiceViewDTO convert(C7nAppServiceVO c7nAppServiceVO) {
        return new BaseC7nAppServiceViewDTO()
                .setRepositoryId(c7nAppServiceVO.getId())
                .setRepositoryName(c7nAppServiceVO.getName())
                .setRepositoryCode(c7nAppServiceVO.getCode())
                .setType(c7nAppServiceVO.getType())
                .setImgUrl(c7nAppServiceVO.getImgUrl())
                .setGlProjectId(Optional.ofNullable(c7nAppServiceVO.getGitlabProjectId())
                        .map(Math::toIntExact)
                        .orElse(null));
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public BaseC7nAppServiceViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public BaseC7nAppServiceViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public String getRepositoryCode() {
        return repositoryCode;
    }

    public BaseC7nAppServiceViewDTO setRepositoryCode(String repositoryCode) {
        this.repositoryCode = repositoryCode;
        return this;
    }

    public String getType() {
        return type;
    }

    public BaseC7nAppServiceViewDTO setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public BaseC7nAppServiceViewDTO setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public BaseC7nAppServiceViewDTO setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }
}
