package org.hrds.rducm.gitlab.api.controller.validator;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.EnumUtils;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApprovalCreateDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApprovalRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.enums.ApplicantTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.ApprovalStateEnum;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
@Component
public class RdmMemberApprovalValidator {
    @Autowired
    private RdmMemberApprovalRepository rdmMemberApprovalRepository;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    public void validateCreateDTO(Long projectId, MemberApprovalCreateDTO memberApprovalCreateDTO) {
        Long repositoryId = memberApprovalCreateDTO.getRepositoryId();
        Long applicantUserId = memberApprovalCreateDTO.getApplicantUserId();
        String applicantType = memberApprovalCreateDTO.getApplicantType();

        Integer accessLevel = memberApprovalCreateDTO.getAccessLevel();

        // 校验是否有为待审批的申请
        RdmMemberApproval dbRdmMemberApproval = rdmMemberApprovalRepository.selectOneWithPending(projectId, repositoryId, applicantUserId);
        AssertExtensionUtils.isNull(dbRdmMemberApproval, "error.member.approval.exist");

        // 校验成员是否已存在
        RdmMember dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, applicantUserId);

        ApplicantTypeEnum applicantTypeEnum = EnumUtils.getEnum(ApplicantTypeEnum.class, applicantType);
        switch (applicantTypeEnum) {
            case MEMBER_JOIN: {
                AssertExtensionUtils.isNull(dbMember, "error.member.exist");
                break;
            }
            case MEMBER_PERMISSION_CHANGE: {
                // 成员需存在
                AssertExtensionUtils.notNull(dbMember, "error.member.not.exist");

                // 旧权限和新权限不能相同
                if (dbMember.getGlAccessLevel().equals(accessLevel)) {
                    throw new CommonException("error.old.new.access.level.equal");
                }

                // 设置旧权限
                memberApprovalCreateDTO.setOldAccessLevel(dbMember.getGlAccessLevel());
                break;
            }
            default:
                break;
        }
    }

    public void validatePass(Long id) {
        // 只有待审批状态才允许操作
        RdmMemberApproval dbMemberApproval = rdmMemberApprovalRepository.selectByPrimaryKey(id);

        if(!dbMemberApproval.getApprovalState().equals(ApprovalStateEnum.PENDING.getCode())) {
            throw new CommonException("error.approval.state.not.pending");
        }
    }

    public void validateRefuse(Long id) {
        // 只有待审批状态才允许操作
        RdmMemberApproval dbMemberApproval = rdmMemberApprovalRepository.selectByPrimaryKey(id);

        if(!dbMemberApproval.getApprovalState().equals(ApprovalStateEnum.PENDING.getCode())) {
            throw new CommonException("error.approval.state.not.pending");
        }
    }
}

