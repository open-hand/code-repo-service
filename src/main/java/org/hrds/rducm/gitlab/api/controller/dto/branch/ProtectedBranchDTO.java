
package org.hrds.rducm.gitlab.api.controller.dto.branch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.gitlab4j.api.models.BranchAccessLevel;
import org.gitlab4j.api.utils.JacksonJson;

import java.util.List;
@ApiModel("保护分支")
public class ProtectedBranchDTO {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("是否允许推送-权限级别")
    private List<BranchAccessLevelDTO> pushAccessLevels;
    @ApiModelProperty("是否允许合并-权限级别")
    private List<BranchAccessLevelDTO> mergeAccessLevels;
//    private List<BranchAccessLevelDTO> unprotectAccessLevels;
//    private Boolean codeOwnerApprovalRequired;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BranchAccessLevelDTO> getPushAccessLevels() {
        return pushAccessLevels;
    }

    public ProtectedBranchDTO setPushAccessLevels(List<BranchAccessLevelDTO> pushAccessLevels) {
        this.pushAccessLevels = pushAccessLevels;
        return this;
    }

    public List<BranchAccessLevelDTO> getMergeAccessLevels() {
        return mergeAccessLevels;
    }

    public ProtectedBranchDTO setMergeAccessLevels(List<BranchAccessLevelDTO> mergeAccessLevels) {
        this.mergeAccessLevels = mergeAccessLevels;
        return this;
    }


    public static final boolean isValid(ProtectedBranchDTO branch) {
        return (branch != null && branch.getName() != null);
    }



    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }
}
