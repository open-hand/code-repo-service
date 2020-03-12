package org.hrds.rducm.gitlab.app.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 成员审批表应用服务
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
public interface RdmMemberApprovalAppService {
    /**
     * 审批通过
     *
     * @param id
     * @param objectVersionNumber
     * @param expiresAt
     */
    void passAndHandleMember(Long id, Long objectVersionNumber, Date expiresAt);

    /**
     * 审批拒绝
     *
     * @param id
     * @param objectVersionNumber
     */
    void refuse(Long id, Long objectVersionNumber);
}
