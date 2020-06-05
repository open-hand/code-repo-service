package org.hrds.rducm.gitlab.domain.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.DetectApplicantTypeDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApplicantCreateDTO;

import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
public interface IRdmMemberApplicantService {
    /**
     * 查询成员权限申请记录
     *
     * @param projectId
     * @param pageRequest
     * @param repositoryIds
     * @param applicantUserName 申请人名称(模糊)
     * @param approvalState     审批状态
     * @return
     */
    Page<RdmMemberApplicantViewDTO> pageByOptions(Long projectId,
                                                  PageRequest pageRequest,
                                                  Set<Long> repositoryIds,
                                                  String applicantUserName,
                                                  String approvalState);

    /**
     * 检测申请类型
     * 1. 如果当前用户没有权限, 返回"新成员"
     * 2. 如果当前用户已有权限, 返回"权限变更"
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    DetectApplicantTypeDTO detectApplicantType(Long projectId, Long repositoryId);

    /**
     * 创建成员申请
     *
     * @param organizationId
     * @param projectId
     * @param memberApplicantCreateDTO
     */
    void createApproval(Long organizationId, Long projectId, MemberApplicantCreateDTO memberApplicantCreateDTO);

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
     * @param approvalMessage     审批信息
     */
    void refuse(Long id, Long objectVersionNumber, String approvalMessage);
}
