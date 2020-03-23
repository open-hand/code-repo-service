package org.hrds.rducm.gitlab.api.controller.dto;

import javax.validation.constraints.Future;
import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/23
 */
public class RdmMemberApplicantPassDTO {
    @Future
    private Date expiresAt;

    public Date getExpiresAt() {
        return expiresAt;
    }

    public RdmMemberApplicantPassDTO setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }
}
