package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/2
 */
public class OperationLogViewDTO {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "项目层，项目id", required = true)
    private Long projectId;

    @ApiModelProperty(value = "代码仓库id", required = true)
    private Long repositoryId;

    @ApiModelProperty(value = "操作人，用户id", required = true)
    private Long opUserId;

    @ApiModelProperty(value = "操作人，用户名", required = true)
    private Long opUserName;

    @ApiModelProperty(value = "操作类型(成员管理|分支管理)", required = true)
    private String opType;

    @ApiModelProperty(value = "操作事件类型", required = true)
    private String opEventType;

    @ApiModelProperty(value = "操作内容")
    private String opContent;

    private String opTarget;

    @ApiModelProperty(value = "操作日期", required = true)
    private Date opDate;

//    private String extraParam;

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

    public Long getOpUserName() {
        return opUserName;
    }

    public OperationLogViewDTO setOpUserName(Long opUserName) {
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
}
