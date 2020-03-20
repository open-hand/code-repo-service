package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/20
 */
public class RdmMemberApplicantViewDTO {
    @ApiModelProperty("表ID，主键")
    private Long id;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "代码库id")
    private Long repositoryId;

    @ApiModelProperty(value = "代码库名称")
    private String repositoryName;

    @ApiModelProperty(value = "申请人，用户id")
    private Long applicantUserId;

    @ApiModelProperty(value = "申请人，用户名")
    private String applicantUserName;

    @ApiModelProperty(value = "申请人，头像地址")
    private String applicantImageUrl;

    @ApiModelProperty(value = "申请类型（新成员|权限变更）")
    private String applicantType;

    @ApiModelProperty(value = "申请时间")
    private Date applicantDate;

    @ApiModelProperty(value = "审批人，用户id")
    private Long approvalUserId;

    @ApiModelProperty(value = "审批人，用户名")
    private String approvalUserName;

    @ApiModelProperty(value = "审批人，头像地址")
    private String approvalImageUrl;

    @ApiModelProperty(value = "审批状态（待审批|通过|不通过）")
    private String approvalState;

    @ApiModelProperty(value = "审批时间")
    private Date approvalDate;

    @ApiModelProperty(value = "审批信息")
    private String approvalMessage;

    @ApiModelProperty(value = "权限等级")
    private Integer accessLevel;

    @ApiModelProperty(value = "旧权限等级")
    private Integer oldAccessLevel;

    private Long createdBy;
    private Date creationDate;
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public RdmMemberApplicantViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public RdmMemberApplicantViewDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmMemberApplicantViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public Long getApplicantUserId() {
        return applicantUserId;
    }

    public RdmMemberApplicantViewDTO setApplicantUserId(Long applicantUserId) {
        this.applicantUserId = applicantUserId;
        return this;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public RdmMemberApplicantViewDTO setApplicantType(String applicantType) {
        this.applicantType = applicantType;
        return this;
    }

    public Date getApplicantDate() {
        return applicantDate;
    }

    public RdmMemberApplicantViewDTO setApplicantDate(Date applicantDate) {
        this.applicantDate = applicantDate;
        return this;
    }

    public Long getApprovalUserId() {
        return approvalUserId;
    }

    public RdmMemberApplicantViewDTO setApprovalUserId(Long approvalUserId) {
        this.approvalUserId = approvalUserId;
        return this;
    }

    public String getApprovalState() {
        return approvalState;
    }

    public RdmMemberApplicantViewDTO setApprovalState(String approvalState) {
        this.approvalState = approvalState;
        return this;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public RdmMemberApplicantViewDTO setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    public String getApprovalMessage() {
        return approvalMessage;
    }

    public RdmMemberApplicantViewDTO setApprovalMessage(String approvalMessage) {
        this.approvalMessage = approvalMessage;
        return this;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public RdmMemberApplicantViewDTO setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public Integer getOldAccessLevel() {
        return oldAccessLevel;
    }

    public RdmMemberApplicantViewDTO setOldAccessLevel(Integer oldAccessLevel) {
        this.oldAccessLevel = oldAccessLevel;
        return this;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public RdmMemberApplicantViewDTO setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public RdmMemberApplicantViewDTO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public RdmMemberApplicantViewDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public RdmMemberApplicantViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public String getApplicantUserName() {
        return applicantUserName;
    }

    public RdmMemberApplicantViewDTO setApplicantUserName(String applicantUserName) {
        this.applicantUserName = applicantUserName;
        return this;
    }

    public String getApprovalUserName() {
        return approvalUserName;
    }

    public RdmMemberApplicantViewDTO setApprovalUserName(String approvalUserName) {
        this.approvalUserName = approvalUserName;
        return this;
    }

    public String getApplicantImageUrl() {
        return applicantImageUrl;
    }

    public RdmMemberApplicantViewDTO setApplicantImageUrl(String applicantImageUrl) {
        this.applicantImageUrl = applicantImageUrl;
        return this;
    }

    public String getApprovalImageUrl() {
        return approvalImageUrl;
    }

    public RdmMemberApplicantViewDTO setApprovalImageUrl(String approvalImageUrl) {
        this.approvalImageUrl = approvalImageUrl;
        return this;
    }
}
