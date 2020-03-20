package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/20
 */
public class DetectApplicantTypeDTO {
    @ApiModelProperty("申请类型")
    private String applicantType;
    @ApiModelProperty("旧权限等级")
    private Integer oldAccessLevel;

    public String getApplicantType() {
        return applicantType;
    }

    public DetectApplicantTypeDTO setApplicantType(String applicantType) {
        this.applicantType = applicantType;
        return this;
    }

    public Integer getOldAccessLevel() {
        return oldAccessLevel;
    }

    public DetectApplicantTypeDTO setOldAccessLevel(Integer oldAccessLevel) {
        this.oldAccessLevel = oldAccessLevel;
        return this;
    }
}
