package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/2
 */
public class OperationLogQueryDTO {


//    @ApiModelProperty(value = "代码仓库id", required = true)
//    private Long repositoryId;

    @ApiModelProperty(value = "操作人，用户id")
    private Long opUserId;

    @ApiModelProperty(value = "开始日期")
    private Date startDate;

    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    public Long getOpUserId() {
        return opUserId;
    }

    public OperationLogQueryDTO setOpUserId(Long opUserId) {
        this.opUserId = opUserId;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public OperationLogQueryDTO setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public OperationLogQueryDTO setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
}
