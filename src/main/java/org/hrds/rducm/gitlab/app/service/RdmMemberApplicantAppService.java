package org.hrds.rducm.gitlab.app.service;

import java.util.Date;
import java.util.List;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantPassVO;

/**
 * 成员申请表应用服务
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
public interface RdmMemberApplicantAppService {
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
     * @param approvalMessage
     */
    void refuse(Long id, Long objectVersionNumber, String approvalMessage);

    void batchPassAndHandleMember(List<RdmMemberApplicantPassVO> rdmMemberApplicantPassVOS, Date expiresAt);

    void batchRefuse(List<RdmMemberApplicantPassVO> rdmMemberApplicantPassVOS, String approvalMessage);
}
