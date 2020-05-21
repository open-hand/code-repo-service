package org.hrds.rducm.gitlab.api.controller.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.EnumUtils;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApplicantCreateDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApplicantRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.enums.ApplicantTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.ApprovalStateEnum;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
@Component
public class RdmMemberApplicantValidator {
    @Autowired
    private RdmMemberApplicantRepository rdmMemberApplicantRepository;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    public void validateCreateDTO(Long projectId, MemberApplicantCreateDTO memberApplicantCreateDTO) {
        Long repositoryId = memberApplicantCreateDTO.getRepositoryId();
        // todo 暂时写死
        Long applicantUserId = DetailsHelper.getUserDetails().getUserId();
//        Long applicantUserId = DetailsHelper.UXY.getUserId();
        String applicantType = memberApplicantCreateDTO.getApplicantType();

        Integer accessLevel = memberApplicantCreateDTO.getAccessLevel();

        // 校验是否有为待审批的申请
        RdmMemberApplicant dbRdmMemberApplicant = rdmMemberApplicantRepository.selectOneWithPending(projectId, repositoryId, applicantUserId);
        AssertExtensionUtils.isNull(dbRdmMemberApplicant, "error.member.approval.exist");

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

                // 成员同步状态需为已同步
                dbMember.checkIsSyncGitlab();

                // 旧权限为Owner以上无法申请
                validateOldAccessLevelIsOwner(dbMember.getGlAccessLevel());

                // 旧权限和新权限不能相同
                if (dbMember.getGlAccessLevel().equals(accessLevel)) {
                    throw new CommonException("error.old.new.access.level.equal");
                }

                // 设置旧权限
                memberApplicantCreateDTO.setOldAccessLevel(dbMember.getGlAccessLevel());
                break;
            }
            default:
                break;
        }
    }

    public void validatePass(Long id) {
        // 只有待审批状态才允许操作
        RdmMemberApplicant dbMemberApproval = rdmMemberApplicantRepository.selectByPrimaryKey(id);

        if(!dbMemberApproval.getApprovalState().equals(ApprovalStateEnum.PENDING.getCode())) {
            throw new CommonException("error.approval.state.not.pending");
        }

        // 校验成员是否已存在
        Long projectId = dbMemberApproval.getProjectId();
        Long repositoryId = dbMemberApproval.getRepositoryId();
        Long applicantUserId = dbMemberApproval.getApplicantUserId();
        String applicantType = dbMemberApproval.getApplicantType();

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

                // 成员同步状态需为已同步
                dbMember.checkIsSyncGitlab();
                break;
            }
            default:
                break;
        }
    }

    public void validateRefuse(Long id) {
        // 只有待审批状态才允许操作
        RdmMemberApplicant dbMemberApproval = rdmMemberApplicantRepository.selectByPrimaryKey(id);

        if(!dbMemberApproval.getApprovalState().equals(ApprovalStateEnum.PENDING.getCode())) {
            throw new CommonException("error.approval.state.not.pending");
        }
    }

    /* 通用校验 */

    public static void validateOldAccessLevelIsOwner(Integer oldAccessLevel) {
        // 旧权限为Owner以上无法申请
        if (oldAccessLevel > RdmAccessLevel.MAINTAINER.toValue()) {
            throw new CommonException("error.old.access.level.can.not.update", Objects.requireNonNull(RdmAccessLevel.forValue(oldAccessLevel)).toDesc());
        }
    }

}

