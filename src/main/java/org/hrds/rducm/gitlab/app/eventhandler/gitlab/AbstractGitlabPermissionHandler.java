package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import java.util.Objects;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.enums.UserRoleEnum;
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
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;


    @Autowired
    private C7nDevOpsServiceFacade c7NDevOpsServiceFacade;

    public void gitlabPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord) {
        //1.常规性数据校验
        if (!checkStatus(rdmMemberAuditRecord)) {
            return;
        }
        checkGroup(rdmMemberAuditRecord);
        //获得组的id

        //2.获得当前审计数据用户的角色
        C7nUserVO c7nUserVO = c7nBaseServiceFacade.detailC7nUserOnProjectLevel(rdmMemberAuditRecord.getProjectId(), rdmMemberAuditRecord.getUserId());
        String role = getUserRole(c7nUserVO);
        //3.根据用户角色来修复数据
        permissionRepair(role, rdmMemberAuditRecord);
        // 4. 回写数据
        rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
    }

    private void checkGroup(RdmMemberAuditRecord rdmMemberAuditRecord) {
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(rdmMemberAuditRecord.getProjectId());
        if (c7nDevopsProjectVO == null) {
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(rdmMemberAuditRecord.getId());
            return;
        }
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());
        // gitlab 上组不存在
        if (glGroupId == null) {
            rdmMemberAuditRecordRepository.deleteByPrimaryKey(rdmMemberAuditRecord.getId());
            return;
        }
        rdmMemberAuditRecord.setgGroupId(glGroupId);
    }

    protected abstract void permissionRepair(String role, RdmMemberAuditRecord rdmMemberAuditRecord);

    private String getUserRole(C7nUserVO c7nUserVO) {
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
                return UserRoleEnum.PROJECT_MEMBER.getValue();
            }
        } else {
            return UserRoleEnum.NON_PROJECT_MEMBER.getValue();
        }
    }

    private boolean checkStatus(RdmMemberAuditRecord rdmMemberAuditRecord) {
        Long userId = rdmMemberAuditRecord.getUserId();
        //如果userId为null 猪齿鱼导入用户失败，导致猪齿鱼里没有这个用户
        if (Objects.isNull(userId)) {
            //如果userId不存在，这个数据就是异常的数据，直接同步
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return Boolean.FALSE;
        }
        Integer glUserId = rdmMemberAuditRecord.getGlUserId();
        if (Objects.isNull(glUserId)) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return Boolean.FALSE;
        }

        Integer glProjectId = rdmMemberAuditRecord.getGlProjectId();
        // gitlab的项目id 不存在
        if (glProjectId == null) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return Boolean.FALSE;
        }
        C7nDevopsProjectVO c7nDevopsProjectVO = c7NDevOpsServiceFacade.detailDevopsProjectById(rdmMemberAuditRecord.getProjectId());
        if (c7nDevopsProjectVO == null) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    protected boolean isProjectMember(C7nUserVO c7nUserVO) {
        return c7nUserVO != null;
    }


    protected abstract RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord);


}
