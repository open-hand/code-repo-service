
package org.hrds.rducm.gitlab.infra.client.gitlab.model;

import java.util.List;

import org.gitlab4j.api.models.BranchAccessLevel;
import org.gitlab4j.api.utils.JacksonJson;

public class ProtectedBranch {

    private String name;
    private List<org.gitlab4j.api.models.BranchAccessLevel> pushAccessLevels;
    private List<org.gitlab4j.api.models.BranchAccessLevel> mergeAccessLevels;
    private List<org.gitlab4j.api.models.BranchAccessLevel> unprotectAccessLevels;
    private Boolean codeOwnerApprovalRequired;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<org.gitlab4j.api.models.BranchAccessLevel> getPushAccessLevels() {
        return this.pushAccessLevels;
    }

    public void setPushAccessLevels(List<org.gitlab4j.api.models.BranchAccessLevel> pushAccessLevels) {
        this.pushAccessLevels = pushAccessLevels;
    }

    public List<org.gitlab4j.api.models.BranchAccessLevel> getMergeAccessLevels() {
        return this.mergeAccessLevels;
    }

    public void setMergeAccessLevels(List<org.gitlab4j.api.models.BranchAccessLevel> mergeAccessLevels) {
        this.mergeAccessLevels = mergeAccessLevels;
    }

    public List<org.gitlab4j.api.models.BranchAccessLevel> getUnprotectAccessLevels() {
        return unprotectAccessLevels;
    }

    public void setUnprotectAccessLevels(List<org.gitlab4j.api.models.BranchAccessLevel> unprotectAccessLevels) {
        this.unprotectAccessLevels = unprotectAccessLevels;
    }

    public static final boolean isValid(ProtectedBranch branch) {
        return (branch != null && branch.getName() != null);
    }

    public ProtectedBranch withName(String name) {
        this.name = name;
        return this;
    }

    public ProtectedBranch withPushAccessLevels(List<org.gitlab4j.api.models.BranchAccessLevel> pushAccessLevels) {
        this.pushAccessLevels = pushAccessLevels;
        return this;
    }

    public ProtectedBranch withMergeAccessLevels(List<BranchAccessLevel> mergeAccessLevels) {
        this.mergeAccessLevels = mergeAccessLevels;
        return this;
    }

    public Boolean getCodeOwnerApprovalRequired() {
        return codeOwnerApprovalRequired;
    }

    public void setCodeOwnerApprovalRequired(Boolean codeOwnerApprovalRequired) {
        this.codeOwnerApprovalRequired = codeOwnerApprovalRequired;
    }

    public ProtectedBranch withCodeOwnerApprovalRequired(Boolean codeOwnerApprovalRequired) {
        this.codeOwnerApprovalRequired = codeOwnerApprovalRequired;
        return this;
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }
}
