package org.hrds.rducm.gitlab.api.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.Future;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by wangxiang on 2021/5/18
 */
public class RdmMemberApplicantPassVO {
    @ApiModelProperty("申请记录")
    @Encrypt
    private Long id;
    @ApiModelProperty("obj")
    private Long objectVersionNumber;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
