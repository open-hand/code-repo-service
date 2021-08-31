package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import java.util.Objects;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangxiang on 2021/8/26
 */
public abstract class AbstractGitlabPermissionHandler implements GitlabPermissionHandler {

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;

    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;


    public void gitlabPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord) {
        Long userId = rdmMemberAuditRecord.getUserId();
        Integer glProjectId = rdmMemberAuditRecord.getGlProjectId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId) || Objects.isNull(glProjectId)) {
            //如果userId不存在，这个数据就是异常的数据，直接同步
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return;
        }
        Integer glUserId = getGlUserId(rdmMemberAuditRecord, userId);
        if (Objects.isNull(glUserId)) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return;
        }
        C7nDevopsProjectVO c7nDevopsProjectVO = c7nDevOpsServiceFacade.detailDevopsProjectById(rdmMemberAuditRecord.getProjectId());
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());
        //查询choerodon权限
        RdmMember dbRdmMember = getDbRdmMember(rdmMemberAuditRecord);
        Boolean isOrgAdmin = c7nBaseServiceFacade.checkIsOrgAdmin(rdmMemberAuditRecord.getOrganizationId(), userId);
        C7nUserVO c7nUserVO = c7nBaseServiceFacade.detailC7nUserOnProjectLevel(rdmMemberAuditRecord.getProjectId(), userId);
        //查询用户在gitlab中group的权限
        Member groupGlMember = queryGroupGlMember(glGroupId, glUserId);
        //修复组织管理员
        if (isOrgAdmin) {
            orgAdminPermissionRepair(dbRdmMember, rdmMemberAuditRecord, groupGlMember, c7nUserVO);
        }
        //修复项目所有者权限
        projectOwnerMemberPermissionRepair(rdmMemberAuditRecord, c7nUserVO, dbRdmMember, groupGlMember);
        //修复项目用户权限
        projectMemberPermissionRepair(rdmMemberAuditRecord, dbRdmMember, groupGlMember, c7nUserVO);
        //修复项目外成员权限
        nonProjectMemberPermissionRepair(rdmMemberAuditRecord, dbRdmMember, groupGlMember, c7nUserVO);
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
    }


    protected abstract void nonProjectMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member groupGlMember, C7nUserVO c7nUserVO);

    protected abstract void projectMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member groupGlMember, C7nUserVO c7nUserVO);

    protected abstract void projectOwnerMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, C7nUserVO c7nUserVO, RdmMember rdmMember, Member groupGlMember);

    protected abstract void orgAdminPermissionRepair(RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord, Member member, C7nUserVO c7nUserVO);


    protected boolean isProjectMember(C7nUserVO c7nUserVO) {
        return c7nUserVO != null;
    }

    protected void updateGitlabGroupMemberWithOwner(Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (groupGlMember == null) {
            // 添加
            gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        } else if (!groupGlMember.getAccessLevel().toValue().equals(AccessLevel.OWNER.toValue())) {
            // 更新
            gitlabGroupFixApi.updateMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        }
    }

    private Member queryGroupGlMember(Integer glGroupId, Integer glUserId) {
        return gitlabGroupFixApi.getMember(glGroupId, glUserId);
    }

    private Integer getGlUserId(RdmMemberAuditRecord rdmMemberAuditRecord, Long userId) {
        if (rdmMemberAuditRecord.getGlUserId() == null) {
            Integer toGlUserId = c7nBaseServiceFacade.userIdToGlUserId(userId);
            rdmMemberAuditRecord.setGlUserId(toGlUserId);
            return toGlUserId;
        } else {
            return rdmMemberAuditRecord.getGlUserId();
        }
    }

    protected abstract RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord);


}
