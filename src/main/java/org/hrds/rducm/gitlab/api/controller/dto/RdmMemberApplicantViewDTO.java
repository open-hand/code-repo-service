package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseC7nUserViewDTO;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/20
 */
public class RdmMemberApplicantViewDTO {
    @ApiModelProperty("表ID，主键")
    @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMA)
    private Long id;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "代码库id")
    private Long repositoryId;

    @ApiModelProperty(value = "代码库名称")
    private String repositoryName;

    @ApiModelProperty(value = "申请人")
    private BaseC7nUserViewDTO applicantUser;

    @ApiModelProperty(value = "申请类型（新成员|权限变更）")
    private String applicantType;

    @ApiModelProperty(value = "申请时间")
    private Date applicantDate;

    @ApiModelProperty(value = "审批人")
    private BaseC7nUserViewDTO approvalUser;

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

    public String getRepositoryName() {
        return repositoryName;
    }

    public RdmMemberApplicantViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public BaseC7nUserViewDTO getApplicantUser() {
        return applicantUser;
    }

    public RdmMemberApplicantViewDTO setApplicantUser(BaseC7nUserViewDTO applicantUser) {
        this.applicantUser = applicantUser;
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

    public BaseC7nUserViewDTO getApprovalUser() {
        return approvalUser;
    }

    public RdmMemberApplicantViewDTO setApprovalUser(BaseC7nUserViewDTO approvalUser) {
        this.approvalUser = approvalUser;
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
}
