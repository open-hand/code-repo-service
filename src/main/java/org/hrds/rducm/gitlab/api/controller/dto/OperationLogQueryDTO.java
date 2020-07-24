package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;
import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/2
 */
public class OperationLogQueryDTO {
    @ApiModelProperty(value = "操作人，用户id")
    @Encrypt
    private Long opUserId;

    @ApiModelProperty(value = "开始日期")
    private Date startDate;

    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    @ApiModelProperty(value = "操作事件类型")
    private List<String> opEventTypes;

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

    public List<String> getOpEventTypes() {
        return opEventTypes;
    }

    public OperationLogQueryDTO setOpEventTypes(List<String> opEventTypes) {
        this.opEventTypes = opEventTypes;
        return this;
    }
}
