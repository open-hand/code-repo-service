package org.hrds.rducm.gitlab.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 操作日志表
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
@ApiModel("操作日志表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_operation_log")
public class RdmOperationLog extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_OP_USER_ID = "opUserId";
    public static final String FIELD_OP_TYPE = "opType";
    public static final String FIELD_OP_CONTENT = "opContent";
    public static final String FIELD_OP_ACTION = "opAction";
    public static final String FIELD_OP_DATE = "opDate";
    public static final String FIELD_EVENT_TYPE = "eventType";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("主键")
    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "项目层，项目id", required = true)
    @NotNull
    private Long projectId;

    @ApiModelProperty(value = "代码仓库id", required = true)
    @NotNull
    private Long repositoryId;

    @ApiModelProperty(value = "操作人，用户id", required = true)
    @NotNull
    private Long opUserId;

    @ApiModelProperty(value = "操作类型(成员管理|分支管理)", required = true)
    @NotBlank
    private String opType;

    @ApiModelProperty(value = "操作事件类型", required = true)
    @NotBlank
    private String opEventType;

    @ApiModelProperty(value = "操作内容")
    private String opContent;

    private String opTarget;

    @ApiModelProperty(value = "操作日期", required = true)
    @NotNull
    private Date opDate;

    private String extraParam;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getId() {
        return id;
    }

    public RdmOperationLog setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public RdmOperationLog setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmOperationLog setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getOpUserId() {
        return opUserId;
    }

    public RdmOperationLog setOpUserId(Long opUserId) {
        this.opUserId = opUserId;
        return this;
    }

    public String getOpType() {
        return opType;
    }

    public RdmOperationLog setOpType(String opType) {
        this.opType = opType;
        return this;
    }

    public String getOpContent() {
        return opContent;
    }

    public RdmOperationLog setOpContent(String opContent) {
        this.opContent = opContent;
        return this;
    }

    public Date getOpDate() {
        return opDate;
    }

    public RdmOperationLog setOpDate(Date opDate) {
        this.opDate = opDate;
        return this;
    }

    public String getOpEventType() {
        return opEventType;
    }

    public RdmOperationLog setOpEventType(String opEventType) {
        this.opEventType = opEventType;
        return this;
    }

    public String getOpTarget() {
        return opTarget;
    }

    public RdmOperationLog setOpTarget(String opTarget) {
        this.opTarget = opTarget;
        return this;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public RdmOperationLog setExtraParam(String extraParam) {
        this.extraParam = extraParam;
        return this;
    }
}
