package org.hrds.rducm.gitlab.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApprovalCreateDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
public interface IRdmMemberApplicantService {
    /**
     * 查询成员权限审批记录
     *
     * @param projectId
     * @param pageRequest
     * @return
     */
    PageInfo<RdmMemberApplicant> pageByOptions(Long projectId, PageRequest pageRequest);

    /**
     * 检测申请类型
     * 1. 如果当前用户没有权限, 返回"新成员"
     * 2. 如果当前用户已有权限, 返回"权限变更"
     *
     * @param projectId
     * @param repositoryId
     */
    String detectApplicantType(Long projectId, Long repositoryId);

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
     * @param approvalMessage 审批信息
     */
    void refuse(Long id, Long objectVersionNumber, String approvalMessage);
}
