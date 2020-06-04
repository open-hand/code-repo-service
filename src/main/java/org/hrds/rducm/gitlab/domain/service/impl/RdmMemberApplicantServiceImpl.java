package org.hrds.rducm.gitlab.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.DetectApplicantTypeDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberApplicantViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApplicantCreateDTO;
import org.hrds.rducm.gitlab.api.controller.validator.RdmMemberApplicantValidator;
import org.hrds.rducm.gitlab.app.assembler.RdmMemberApplicantAssembler;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.facade.MessageClientFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApplicantRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.facade.IC7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApplicantService;
import org.hrds.rducm.gitlab.infra.enums.ApplicantTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.ApprovalStateEnum;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Set;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
@Service
public class RdmMemberApplicantServiceImpl implements IRdmMemberApplicantService {
    private static final Logger logger = LoggerFactory.getLogger(RdmMemberApplicantServiceImpl.class);

    @Autowired
    private RdmMemberApplicantRepository rdmMemberApplicantRepository;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private RdmMemberApplicantAssembler rdmMemberApplicantAssembler;
    @Autowired
    private IC7nBaseServiceFacade ic7NBaseServiceFacade;
    @Autowired
    private MessageClientFacade messageClientFacade;

    @Override
    public Page<RdmMemberApplicantViewDTO> pageByOptions(Long projectId,
                                                             PageRequest pageRequest,
                                                             Set<Long> repositoryIds,
                                                             String applicantUserName,
                                                             String approvalState) {
        Condition condition = Condition.builder(RdmMemberApplicant.class)
                .where(Sqls.custom()
                        .andEqualTo(RdmMemberApplicant.FIELD_PROJECT_ID, projectId)
                        .andIn(RdmMemberApplicant.FIELD_REPOSITORY_ID, repositoryIds, true)
                        .andEqualTo(RdmMemberApplicant.FIELD_APPROVAL_STATE, approvalState, true))
                .build();

        // 调用外部接口模糊查询
        if (!StringUtils.isEmpty(applicantUserName)) {
            Set<Long> userIdsSet = ic7NBaseServiceFacade.listC7nUserIdsByNameOnProjectLevel(projectId, applicantUserName, null);

            if (userIdsSet.isEmpty()) {
                return new Page<>();
            }

            condition.and().andIn(RdmMemberApplicant.FIELD_APPLICANT_USER_ID, userIdsSet);
        }

        Page<RdmMemberApplicant> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberApplicantRepository.selectByCondition(condition));

        // 转换查询结果
        return rdmMemberApplicantAssembler.pageToRdmMemberApplicantViewDTO(page);
    }

    @Override
    public DetectApplicantTypeDTO detectApplicantType(Long projectId, Long repositoryId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();

        RdmMember dbMember = rdmMemberRepository.selectOneByUk(projectId, repositoryId, userId);
        if (dbMember == null) {
            return new DetectApplicantTypeDTO()
                    .setApplicantType(ApplicantTypeEnum.MEMBER_JOIN.getCode());
        } else {
            // 旧权限为Owner以上无法申请
            RdmMemberApplicantValidator.validateOldAccessLevelIsOwner(dbMember.getGlAccessLevel());

            return new DetectApplicantTypeDTO()
                    .setApplicantType(ApplicantTypeEnum.MEMBER_PERMISSION_CHANGE.getCode())
                    .setOldAccessLevel(dbMember.getGlAccessLevel());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApproval(Long organizationId, Long projectId, MemberApplicantCreateDTO memberApplicantCreateDTO) {
        // <1> 转换
        RdmMemberApplicant param = ConvertUtils.convertObject(memberApplicantCreateDTO, RdmMemberApplicant.class);

        // <2> 创建成员权限申请
        // 申请人
        Long applicantUserId = DetailsHelper.getUserDetails().getUserId();

        param.setApplicantUserId(applicantUserId);
        param.setOrganizationId(organizationId);
        param.setProjectId(projectId);
        param.setApplicantDate(new Date());
        param.setApprovalState(ApprovalStateEnum.PENDING.getCode());
        rdmMemberApplicantRepository.insertSelective(param);

        // <3> 发送站内信, 通知审批人
        logger.info("发送站内信, 通知审批人");
        messageClientFacade.sendApprovalMessage(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pass(Long id, Long objectVersionNumber) {
        // 审批通过
        Long userId = DetailsHelper.getUserDetails().getUserId();

        RdmMemberApplicant dbMemberApproval = new RdmMemberApplicant();
        dbMemberApproval.setId(id);
        dbMemberApproval.setApprovalState(ApprovalStateEnum.APPROVED.getCode());
        dbMemberApproval.setApprovalUserId(userId);
        dbMemberApproval.setApprovalDate(new Date());
        dbMemberApproval.setObjectVersionNumber(objectVersionNumber);

        rdmMemberApplicantRepository.updateByPrimaryKeySelective(dbMemberApproval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuse(Long id, Long objectVersionNumber, String approvalMessage) {
        // 审批拒绝
        Long userId = DetailsHelper.getUserDetails().getUserId();

        RdmMemberApplicant dbMemberApproval = new RdmMemberApplicant();
        dbMemberApproval.setId(id);
        dbMemberApproval.setApprovalState(ApprovalStateEnum.REJECTED.getCode());
        dbMemberApproval.setApprovalUserId(userId);
        dbMemberApproval.setApprovalDate(new Date());
        dbMemberApproval.setApprovalMessage(approvalMessage);
        dbMemberApproval.setObjectVersionNumber(objectVersionNumber);

        rdmMemberApplicantRepository.updateByPrimaryKeySelective(dbMemberApproval);
    }
}
