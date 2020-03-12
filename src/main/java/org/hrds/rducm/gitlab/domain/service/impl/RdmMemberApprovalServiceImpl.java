package org.hrds.rducm.gitlab.domain.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hrds.rducm.gitlab.api.controller.dto.member.MemberApprovalCreateDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApproval;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberApprovalRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberApprovalService;
import org.hrds.rducm.gitlab.infra.enums.ApprovalStateEnum;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.hrds.rducm.gitlab.infra.util.PageConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
@Service
public class RdmMemberApprovalServiceImpl implements IRdmMemberApprovalService {
    @Autowired
    private RdmMemberApprovalRepository rdmMemberApprovalRepository;

    @Override
    public PageInfo<RdmMemberApproval> pageByOptions(Long projectId, PageRequest pageRequest) {
        Page<RdmMemberApproval> page = PageHelper.doPageAndSort(pageRequest, () -> rdmMemberApprovalRepository.selectAll());
        return PageConvertUtils.convert(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApproval(Long projectId, MemberApprovalCreateDTO memberApprovalCreateDTO) {
        // <1> 转换
        RdmMemberApproval param = ConvertUtils.convertObject(memberApprovalCreateDTO, RdmMemberApproval.class);

        // <2> 创建成员权限申请
        param.setProjectId(projectId);
        param.setApplicantDate(new Date());
        param.setApprovalState(ApprovalStateEnum.PENDING.getCode());
        rdmMemberApprovalRepository.insertSelective(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pass(Long id, Long objectVersionNumber) {
        // 审批通过
        Long userId = DetailsHelper.getUserDetails().getUserId();

        RdmMemberApproval dbMemberApproval = new RdmMemberApproval();
        dbMemberApproval.setId(id);
        dbMemberApproval.setApprovalState(ApprovalStateEnum.APPROVED.getCode());
        dbMemberApproval.setApprovalUserId(userId);
        dbMemberApproval.setApprovalDate(new Date());
        dbMemberApproval.setObjectVersionNumber(objectVersionNumber);

        rdmMemberApprovalRepository.updateByPrimaryKeySelective(dbMemberApproval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuse(Long id, Long objectVersionNumber) {
        // 审批拒绝
        Long userId = DetailsHelper.getUserDetails().getUserId();

        RdmMemberApproval dbMemberApproval = new RdmMemberApproval();
        dbMemberApproval.setId(id);
        dbMemberApproval.setApprovalState(ApprovalStateEnum.REJECTED.getCode());
        dbMemberApproval.setApprovalUserId(userId);
        dbMemberApproval.setApprovalDate(new Date());
        dbMemberApproval.setObjectVersionNumber(objectVersionNumber);

        rdmMemberApprovalRepository.updateByPrimaryKeySelective(dbMemberApproval);
    }
}
