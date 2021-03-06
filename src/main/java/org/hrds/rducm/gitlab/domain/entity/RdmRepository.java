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
import javax.validation.constraints.NotNull;

/**
 * @author ying.xie@hand-china.com 2020-02-27 19:19:50
 */
@ApiModel("")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "rducm_gitlab_repository")
public class RdmRepository extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_REPOSITORY_ID = "repositoryId";
    public static final String FIELD_REPOSITORY_NAME = "repositoryName";
    public static final String FIELD_GL_PROJECT_ID = "glProjectId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Long organizationId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "代码库id", required = true)
    @NotNull
    private Long repositoryId;
    @ApiModelProperty(value = "代码库名称（即应用服务名称）")
    private String repositoryName;
    @ApiModelProperty(value = "Gitlab项目id（代码库）", required = true)
    @NotNull
    private Integer glProjectId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getId() {
        return id;
    }

    public RdmRepository setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public RdmRepository setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public RdmRepository setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RdmRepository setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public RdmRepository setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public RdmRepository setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }
}
