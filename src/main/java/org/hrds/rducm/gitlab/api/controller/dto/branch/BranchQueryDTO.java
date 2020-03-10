package org.hrds.rducm.gitlab.api.controller.dto.branch;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/10
 */
public class BranchQueryDTO {
    @ApiModelProperty(value = "是否排除保护分支", dataType = "boolean")
    private Boolean excludeProtectedFlag;

    public Boolean getExcludeProtectedFlag() {
        return excludeProtectedFlag;
    }

    public BranchQueryDTO setExcludeProtectedFlag(Boolean excludeProtectedFlag) {
        this.excludeProtectedFlag = excludeProtectedFlag;
        return this;
    }
}
