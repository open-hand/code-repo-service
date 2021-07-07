package org.hrds.rducm.gitlab.app.eventhandler.payload;

import java.util.Set;

/**
 * Created by wangxiang on 2021/7/7
 */
public class ProjectAuditPayload {

    private Long organizationId;

    private Long projectId;

    private Set<Long> recordIds;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(Set<Long> recordIds) {
        this.recordIds = recordIds;
    }
}
