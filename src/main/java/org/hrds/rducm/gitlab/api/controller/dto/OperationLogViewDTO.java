package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nProjectViewDTO;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/2
 */
public class OperationLogViewDTO {
    @ApiModelProperty("主键")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "项目层，项目id", required = true)
    private Long projectId;

    @ApiModelProperty("项目信息")
    private BaseC7nProjectViewDTO project;

    @ApiModelProperty(value = "代码仓库id", required = true)
    @Encrypt
    private Long repositoryId;

    @ApiModelProperty(value = "代码仓库名称")
    private String repositoryName;

    @ApiModelProperty(value = "代码仓库图片")
    private String repositoryImageUrl;

    @ApiModelProperty(value = "操作人，用户id", required = true)
    @Encrypt
    private Long opUserId;

    @ApiModelProperty(value = "操作人，用户名", required = true)
    private String opUserName;

    @ApiModelProperty(value = "操作人, 头像地址")
    private String opUserImageUrl;

    @ApiModelProperty(value = "操作类型(成员管理|分支管理)", required = true)
    private String opType;

    @ApiModelProperty(value = "操作事件类型", required = true)
    private String opEventType;

    @ApiModelProperty(value = "操作内容")
    private String opContent;

    private String opTarget;

    @ApiModelProperty(value = "操作日期", required = true)
    private Date opDate;

    public Long getId() {
        return id;
    }

    public OperationLogViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public OperationLogViewDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public OperationLogViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getOpUserId() {
        return opUserId;
    }

    public OperationLogViewDTO setOpUserId(Long opUserId) {
        this.opUserId = opUserId;
        return this;
    }

    public String getOpUserName() {
        return opUserName;
    }

    public OperationLogViewDTO setOpUserName(String opUserName) {
        this.opUserName = opUserName;
        return this;
    }

    public String getOpType() {
        return opType;
    }

    public OperationLogViewDTO setOpType(String opType) {
        this.opType = opType;
        return this;
    }

    public String getOpEventType() {
        return opEventType;
    }

    public OperationLogViewDTO setOpEventType(String opEventType) {
        this.opEventType = opEventType;
        return this;
    }

    public String getOpContent() {
        return opContent;
    }

    public OperationLogViewDTO setOpContent(String opContent) {
        this.opContent = opContent;
        return this;
    }

    public String getOpTarget() {
        return opTarget;
    }

    public OperationLogViewDTO setOpTarget(String opTarget) {
        this.opTarget = opTarget;
        return this;
    }

    public Date getOpDate() {
        return opDate;
    }

    public OperationLogViewDTO setOpDate(Date opDate) {
        this.opDate = opDate;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public OperationLogViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public String getOpUserImageUrl() {
        return opUserImageUrl;
    }

    public OperationLogViewDTO setOpUserImageUrl(String opUserImageUrl) {
        this.opUserImageUrl = opUserImageUrl;
        return this;
    }

    public String getRepositoryImageUrl() {
        return repositoryImageUrl;
    }

    public OperationLogViewDTO setRepositoryImageUrl(String repositoryImageUrl) {
        this.repositoryImageUrl = repositoryImageUrl;
        return this;
    }

    public BaseC7nProjectViewDTO getProject() {
        return project;
    }

    public OperationLogViewDTO setProject(BaseC7nProjectViewDTO project) {
        this.project = project;
        return this;
    }
}
