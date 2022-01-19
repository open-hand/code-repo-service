package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.UserRoleEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * Created by wangxiang on 2021/8/26
 */
public abstract class AbstractGitlabPermissionHandler implements GitlabPermissionHandler {

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;


    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;


    public void gitlabPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord) {
        //1.常规性数据校验
        if (!checkStatus(rdmMemberAuditRecord)) {
            return;
        }
        //2.获得当前审计数据用户的角色
        C7nUserVO c7nUserVO = c7nBaseServiceFacade.detailC7nUserOnProjectLevel(rdmMemberAuditRecord.getProjectId(), rdmMemberAuditRecord.getUserId());
        String role = getUserRole(rdmMemberAuditRecord.getProjectId(), c7nUserVO);
        //3.根据用户角色来修复数据
        permissionRepair(role, rdmMemberAuditRecord);
        // 4. 回写数据
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
    }

    protected abstract void permissionRepair(String role, RdmMemberAuditRecord rdmMemberAuditRecord);

    private String getUserRole(Long projectId, C7nUserVO c7nUserVO) {
        if (c7nUserVO == null) {
            return UserRoleEnum.NON_PROJECT_MEMBER.getValue();
        }
        Boolean isOrgAdmin = c7nBaseServiceFacade.checkIsOrgAdmin(c7nUserVO.getOrganizationId(), c7nUserVO.getId());
        if (isProjectMember(c7nUserVO)) {
            if (isOrgAdmin) {
                return UserRoleEnum.ORGANIZATION_ADMIN.getValue();
            } else if (c7nUserVO.isProjectAdmin()) {
                return UserRoleEnum.PROJECT_ADMIN.getValue();
            } else {
                //项目成员和自定义角色在这里处理，
                //这里看用户的gitlab标签是不是有，如果有则是自定义的Owner，如果不是则按项目成员的角色处理
                List<C7nUserVO> gitlabOwners = c7nBaseServiceFacade.listCustomGitlabOwnerLableUser(projectId, "GITLAB_OWNER");
                if (CollectionUtils.isEmpty(gitlabOwners)) {
                    return UserRoleEnum.PROJECT_MEMBER.getValue();
                }
                if (gitlabOwners.stream().map(C7nUserVO::getId).collect(Collectors.toList()).contains(c7nUserVO.getId())) {
                    return UserRoleEnum.PROJECT_ADMIN.getValue();
                }
                return UserRoleEnum.PROJECT_MEMBER.getValue();
            }
        } else {
            return UserRoleEnum.NON_PROJECT_MEMBER.getValue();
        }
    }

    private boolean checkStatus(RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (rdmMemberAuditRecord.getSyncFlag()) {
            return Boolean.FALSE;
        }
        Long userId = rdmMemberAuditRecord.getUserId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId)) {
            //如果userId不存在，这个数据就是异常的数据，直接同步
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return Boolean.FALSE;
        }
        Integer glUserId = getGlUserId(rdmMemberAuditRecord, userId);
        if (Objects.isNull(glUserId)) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


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
