package org.hrds.rducm.gitlab.infra.feign.vo;

public class C7nDevopsProjectVO {
    private Long projectId;
    private Long gitlabGroupId;

    public Long getProjectId() {
        return projectId;
    }

    public C7nDevopsProjectVO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Long getGitlabGroupId() {
        return gitlabGroupId;
    }

    public C7nDevopsProjectVO setGitlabGroupId(Long gitlabGroupId) {
        this.gitlabGroupId = gitlabGroupId;
        return this;
    }
}
