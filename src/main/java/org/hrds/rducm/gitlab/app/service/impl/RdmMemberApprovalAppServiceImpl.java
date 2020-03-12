package org.hrds.rducm.gitlab.app.service.impl;

import org.apache.commons.lang3.EnumUtils;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberCreateDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberUpdateDTO;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.app.service.RdmMemberApprovalAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApprovalRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApprovalService;
import org.hrds.rducm.gitlab.infra.enums.ApplicantTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 成员审批表应用服务默认实现
 *
 * @author ying.xie@hand-china.com 2020-03-11 17:29:45
 */
@Service
public class RdmMemberApprovalAppServiceImpl implements RdmMemberApprovalAppService {
    @Autowired
    private IRdmMemberApprovalService iRdmMemberApprovalService;
    @Autowired
    private RdmMemberApprovalRepository rdmMemberApprovalRepository;
    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passAndHandleMember(Long id, Long objectVersionNumber, Date expiresAt) {
        // <1> 查询审批记录
        RdmMemberApproval dbMemberApproval = rdmMemberApprovalRepository.selectByPrimaryKey(id);

        // <2> 新增成员|更新成员
        ApplicantTypeEnum applicantTypeEnum = EnumUtils.getEnum(ApplicantTypeEnum.class, dbMemberApproval.getApplicantType());
        switch (applicantTypeEnum) {
            case MEMBER_JOIN: {
                // 新增成员
                RdmMemberCreateDTO rdmMemberCreateDTO = new RdmMemberCreateDTO();
                rdmMemberCreateDTO.setUserId(dbMemberApproval.getApplicantUserId());
                rdmMemberCreateDTO.setGlAccessLevel(dbMemberApproval.getAccessLevel());
                rdmMemberCreateDTO.setGlExpiresAt(expiresAt);

                rdmMemberAppService.addMember(dbMemberApproval.getProjectId(), dbMemberApproval.getRepositoryId(), rdmMemberCreateDTO);
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
        iRdmMemberApprovalService.pass(id, objectVersionNumber);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuse(Long id, Long objectVersionNumber) {
        // <1> 审批拒绝
        iRdmMemberApprovalService.refuse(id, objectVersionNumber);
    }
}
