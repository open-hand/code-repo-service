package org.hrds.rducm.gitlab.api.controller.dto.repository;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public class RepositoryOverViewDTO {
    private Long repositoryId;
    private String repositoryName;
    private Integer developerCount;
    private String defaultBranch;
    private String visibility;
    private Date lastCommittedDate;
    private Integer approvalsBeforeMergeCount;
    private Date repositoryCreationDate;

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

    public Integer getApprovalsBeforeMergeCount() {
        return approvalsBeforeMergeCount;
    }

    public RepositoryOverViewDTO setApprovalsBeforeMergeCount(Integer approvalsBeforeMergeCount) {
        this.approvalsBeforeMergeCount = approvalsBeforeMergeCount;
        return this;
    }

    public Date getRepositoryCreationDate() {
        return repositoryCreationDate;
    }

    public RepositoryOverViewDTO setRepositoryCreationDate(Date repositoryCreationDate) {
        this.repositoryCreationDate = repositoryCreationDate;
        return this;
    }
}
