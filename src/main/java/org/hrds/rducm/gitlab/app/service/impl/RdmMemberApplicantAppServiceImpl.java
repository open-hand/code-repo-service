package org.hrds.rducm.gitlab.app.service.impl;

import org.apache.commons.lang3.EnumUtils;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberCreateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.hrds.rducm.gitlab.api.controller.validator.RdmMemberApplicantValidator;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.app.service.RdmMemberApplicantAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.facade.MessageClientFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApplicantRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApplicantService;
import org.hrds.rducm.gitlab.infra.enums.ApplicantTypeEnum;
import org.hrds.rducm.gitlab.infra.mapper.MemberApprovalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 成员申请表应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
@Service
public class RdmMemberApplicantAppServiceImpl implements RdmMemberApplicantAppService {

    private static final String PERMISSION_REJECTED = "RDUCM.PERMISSION.REJECTED";
    private static final String PERMISSION_APPROVED = "RDUCM.PERMISSION.APPROVED";

    @Autowired
    private IRdmMemberApplicantService iRdmMemberApplicantService;
    @Autowired
    private RdmMemberApplicantRepository rdmMemberApplicantRepository;
    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private RdmMemberApplicantValidator rdmMemberApplicantValidator;
    @Autowired
    private MessageClientFacade messageClientFacade;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passAndHandleMember(Long id, Long objectVersionNumber, Date expiresAt) {
        // <0> 校验
        rdmMemberApplicantValidator.validatePass(id);

        // <1> 查询审批记录
        RdmMemberApplicant dbMemberApproval = rdmMemberApplicantRepository.selectByPrimaryKey(id);

        // <2> 处理成员, 分为新增成员|更新成员
        ApplicantTypeEnum applicantTypeEnum = EnumUtils.getEnum(ApplicantTypeEnum.class, dbMemberApproval.getApplicantType());
        switch (applicantTypeEnum) {
            case MEMBER_JOIN: {
                // 新增成员
                RdmMemberCreateDTO rdmMemberCreateDTO = new RdmMemberCreateDTO();
                rdmMemberCreateDTO.setUserId(dbMemberApproval.getApplicantUserId());
                rdmMemberCreateDTO.setGlAccessLevel(dbMemberApproval.getAccessLevel());
                rdmMemberCreateDTO.setGlExpiresAt(expiresAt);

                rdmMemberAppService.addMember(dbMemberApproval.getOrganizationId(), dbMemberApproval.getProjectId(), dbMemberApproval.getRepositoryId(), rdmMemberCreateDTO);
                break;
            }
            case MEMBER_PERMISSION_CHANGE: {
                // 更新成员
                RdmMember dbMember = rdmMemberRepository.selectOneByUk(dbMemberApproval.getProjectId(), dbMemberApproval.getRepositoryId(), dbMemberApproval.getApplicantUserId());
                RdmMemberUpdateDTO rdmMemberUpdateDTO = new RdmMemberUpdateDTO();
                rdmMemberUpdateDTO.setGlAccessLevel(dbMemberApproval.getAccessLevel());
                rdmMemberUpdateDTO.setGlExpiresAt(expiresAt);
                rdmMemberUpdateDTO.setObjectVersionNumber(dbMember.getObjectVersionNumber());

                rdmMemberAppService.updateMember(dbMember.getId(), rdmMemberUpdateDTO);
                break;
            }
            default:
                throw new RuntimeException();
        }

        // <3> 审批通过
        iRdmMemberApplicantService.pass(id, objectVersionNumber);

        //审批拒绝发送站内信给申请人
        messageClientFacade.sendApprovalNotice(id, PERMISSION_APPROVED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuse(Long id, Long objectVersionNumber, String approvalMessage) {
        // <0> 校验
        rdmMemberApplicantValidator.validateRefuse(id);

        // <1> 审批拒绝
        iRdmMemberApplicantService.refuse(id, objectVersionNumber, approvalMessage);

        //审批拒绝发送站内信给申请人
        messageClientFacade.sendApprovalNotice(id, PERMISSION_REJECTED);

    }
}
