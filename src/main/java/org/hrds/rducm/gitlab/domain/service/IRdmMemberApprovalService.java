package org.hrds.rducm.gitlab.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApprovalCreateDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
public interface IRdmMemberApprovalService {
    /**
     * 查询成员权限审批记录
     *
     * @param projectId
     * @param pageRequest
     * @return
     */
    PageInfo<RdmMemberApproval> pageByOptions(Long projectId, PageRequest pageRequest);

    /**
     * 创建成员申请
     *
     * @param projectId
     * @param memberApprovalCreateDTO
     */
    void createApproval(Long projectId, MemberApprovalCreateDTO memberApprovalCreateDTO);

    /**
     * 审批通过
     *
     * @param id
     * @param objectVersionNumber
     */
    void pass(Long id, Long objectVersionNumber);

    /**
     * 审批拒绝
     *
     * @param id
     * @param objectVersionNumber
     */
    void refuse(Long id, Long objectVersionNumber);
}
