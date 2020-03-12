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
 * 成员审批表
 *
 * @author ying.xie@hand-china.com 2020-03-12 10:52:57
 */
@ApiModel("成员审批表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_member_approval")
public class RdmMemberApproval extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_APPLICANT_USER_ID = "applicantUserId";
    public static final String FIELD_APPLICANT_TYPE = "applicantType";
    public static final String FIELD_APPLICANT_DATE = "applicantDate";
    public static final String FIELD_APPROVAL_USER_ID = "approvalUserId";
    public static final String FIELD_APPROVAL_STATE = "approvalState";
    public static final String FIELD_APPROVAL_DATE = "approvalDate";
    public static final String FIELD_ACCESS_LEVEL = "accessLevel";
    public static final String FIELD_OLD_ACCESS_LEVEL = "oldAccessLevel";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键")
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty(value = "项目id", required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "代码库id", required = true)
    @NotNull
    private Long repositoryId;
    @ApiModelProperty(value = "申请人，用户id", required = true)
    @NotNull
    private Long applicantUserId;
    @ApiModelProperty(value = "申请类型（新成员|权限变更）", required = true)
    @NotBlank
    private String applicantType;
    @ApiModelProperty(value = "申请时间", required = true)
    @NotNull
    private Date applicantDate;
    @ApiModelProperty(value = "审批人，用户id")
    private Long approvalUserId;
    @ApiModelProperty(value = "审批状态（待审批|通过）", required = true)
    @NotBlank
    private String approvalState;
    @ApiModelProperty(value = "审批时间")
    private Date approvalDate;
    @ApiModelProperty(value = "权限等级", required = true)
    @NotNull
    private Integer accessLevel;
    @ApiModelProperty(value = "旧权限等级")
    private Integer oldAccessLevel;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 项目id
     */
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * @return 代码库id
     */
    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    /**
     * @return 申请人，用户id
     */
    public Long getApplicantUserId() {
        return applicantUserId;
    }

    public void setApplicantUserId(Long applicantUserId) {
        this.applicantUserId = applicantUserId;
    }

    /**
     * @return 申请类型（新成员|权限变更）
     */
    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    /**
     * @return 申请时间
     */
    public Date getApplicantDate() {
        return applicantDate;
    }

    public void setApplicantDate(Date applicantDate) {
        this.applicantDate = applicantDate;
    }

    /**
     * @return 审批人，用户id
     */
    public Long getApprovalUserId() {
        return approvalUserId;
    }

    public void setApprovalUserId(Long approvalUserId) {
        this.approvalUserId = approvalUserId;
    }

    /**
     * @return 审批状态（待审批|通过）
     */
    public String getApprovalState() {
        return approvalState;
    }

    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * @return 审批时间
     */
    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    /**
     * @return 权限等级
     */
    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * @return 旧权限等级
     */
    public Integer getOldAccessLevel() {
        return oldAccessLevel;
    }

    public void setOldAccessLevel(Integer oldAccessLevel) {
        this.oldAccessLevel = oldAccessLevel;
    }

}
