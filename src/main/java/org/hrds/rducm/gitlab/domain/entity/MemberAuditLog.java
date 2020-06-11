package org.hrds.rducm.gitlab.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.KeyEncryptConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 成员权限审计日志表
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@ApiModel("成员权限审计日志表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_member_audit_log")
public class MemberAuditLog extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_AUDIT_NO = "auditNo";
    public static final String FIELD_AUDIT_COUNT = "auditCount";
    public static final String FIELD_AUDIT_START_DATE = "auditStartDate";
    public static final String FIELD_AUDIT_END_DATE = "auditEndDate";
    public static final String FIELD_AUDIT_DURATION = "auditDuration";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("主键")
    @Id
    @GeneratedValue
    @Encrypt(KeyEncryptConstants.KEY_ENCRYPT_RGMAL)
    private Long id;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "代码库id")
    private Long repositoryId;
    @ApiModelProperty(value = "审计流水号", required = true)
    @NotBlank
    private String auditNo;
    @ApiModelProperty(value = "审计记录总数", required = true)
    @NotNull
    private Integer auditCount;
    @ApiModelProperty(value = "审计开始日期", required = true)
    @NotNull
    private Date auditStartDate;
    @ApiModelProperty(value = "审计结束日期", required = true)
    @NotNull
    private Date auditEndDate;
    @ApiModelProperty(value = "审计耗时", required = true)
    @NotNull
    private Integer auditDuration;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 组织id
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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
     * @return 审计流水号
     */
    public String getAuditNo() {
        return auditNo;
    }

    public void setAuditNo(String auditNo) {
        this.auditNo = auditNo;
    }

    /**
     * @return 审计记录总数
     */
    public Integer getAuditCount() {
        return auditCount;
    }

    public void setAuditCount(Integer auditCount) {
        this.auditCount = auditCount;
    }

    /**
     * @return 审计开始日期
     */
    public Date getAuditStartDate() {
        return auditStartDate;
    }

    public void setAuditStartDate(Date auditStartDate) {
        this.auditStartDate = auditStartDate;
    }

    /**
     * @return 审计结束日期
     */
    public Date getAuditEndDate() {
        return auditEndDate;
    }

    public void setAuditEndDate(Date auditEndDate) {
        this.auditEndDate = auditEndDate;
    }

    /**
     * @return 审计耗时
     */
    public Integer getAuditDuration() {
        return auditDuration;
    }

    public void setAuditDuration(Integer auditDuration) {
        this.auditDuration = auditDuration;
    }

}
