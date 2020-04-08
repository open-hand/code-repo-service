package org.hrds.rducm.gitlab.api.controller.dto.repository;

import io.swagger.annotations.ApiModelProperty;
import org.hrds.rducm.gitlab.infra.constant.ApiInfoConstants;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public class RepositoryOverViewDTO {
    @ApiModelProperty(value = ApiInfoConstants.REPOSITORY_ID)
    private Long repositoryId;
    @ApiModelProperty(value = "代码库名称")
    private String repositoryName;
    @ApiModelProperty(value = "开发成员数量")
    private Integer developerCount;
    @ApiModelProperty(value = "管理者数量, 权限大于等于Maintainer")
    private Integer managerCount;
    @ApiModelProperty(value = "默认分支")
    private String defaultBranch;
    @ApiModelProperty(value = "仓库可见性")
    private String visibility;
    @ApiModelProperty(value = "最近一次提交日期")
    private Date lastCommittedDate;
    @ApiModelProperty(value = "待处理合并请求数")
    private Integer openedMergeRequestCount;
    @ApiModelProperty(value = "代码库创建日期")
    private Date repositoryCreationDate;
    @ApiModelProperty(value = "Gitlab代码库id")
    private Integer glProjectId;

    public Long getRepositoryId() {
        return repositoryId;
    }

    public RepositoryOverViewDTO setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public RepositoryOverViewDTO setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public Integer getDeveloperCount() {
        return developerCount;
    }

    public RepositoryOverViewDTO setDeveloperCount(Integer developerCount) {
        this.developerCount = developerCount;
        return this;
    }

    public Integer getManagerCount() {
        return managerCount;
    }

    public RepositoryOverViewDTO setManagerCount(Integer managerCount) {
        this.managerCount = managerCount;
        return this;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public RepositoryOverViewDTO setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
        return this;
    }

    public String getVisibility() {
        return visibility;
    }

    public RepositoryOverViewDTO setVisibility(String visibility) {
        this.visibility = visibility;
        return this;
    }

    public Date getLastCommittedDate() {
        return lastCommittedDate;
    }

    public RepositoryOverViewDTO setLastCommittedDate(Date lastCommittedDate) {
        this.lastCommittedDate = lastCommittedDate;
        return this;
    }

    public Integer getOpenedMergeRequestCount() {
        return openedMergeRequestCount;
    }

    public RepositoryOverViewDTO setOpenedMergeRequestCount(Integer openedMergeRequestCount) {
        this.openedMergeRequestCount = openedMergeRequestCount;
        return this;
    }

    public Date getRepositoryCreationDate() {
        return repositoryCreationDate;
    }

    public RepositoryOverViewDTO setRepositoryCreationDate(Date repositoryCreationDate) {
        this.repositoryCreationDate = repositoryCreationDate;
        return this;
    }

    public Integer getGlProjectId() {
        return glProjectId;
    }

    public RepositoryOverViewDTO setGlProjectId(Integer glProjectId) {
        this.glProjectId = glProjectId;
        return this;
    }
}
