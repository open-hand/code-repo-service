package org.hrds.rducm.gitlab.api.controller.dto.tag;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/10
 */
public class TagQueryDTO {
    @ApiModelProperty(value = "是否排除保护标记", dataType = "boolean")
    private Boolean excludeProtectedFlag;

    public Boolean getExcludeProtectedFlag() {
        return excludeProtectedFlag;
    }

    public TagQueryDTO setExcludeProtectedFlag(Boolean excludeProtectedFlag) {
        this.excludeProtectedFlag = excludeProtectedFlag;
        return this;
    }
}
