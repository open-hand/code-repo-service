package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import java.util.List;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Created by wangxiang on 2021/10/23
 */
@Component
public class OrganizationAdminPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private RdmMemberAppService rdmMemberAppService;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;

    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;

    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;


    @Override
    public void repairGroupPermissionByRole(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //如果是组织管理员，又是项目成员，需要插入dbMember
        rdmMemberAppService.insertGroupMember(rdmMemberAuditRecord);
        //更新用户在gitlab组的权限为owner
        updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);

    }

    @Override
    public void repairProjectPermissionByRole(Member member, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //如果是组织管理员，在项目层级修复，需要插入dbMember
        rdmMemberAppService.insertGroupMember(rdmMemberAuditRecord);
        Member groupGlMember = null;
        C7nDevopsProjectVO c7nDevopsProjectVO = c7nDevOpsServiceFacade.detailDevopsProjectById(rdmMemberAuditRecord.getProjectId());
        if (c7nDevopsProjectVO != null && c7nDevopsProjectVO.getGitlabGroupId() != null) {
            Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());
            if (rdmMemberAuditRecord.getGlUserId() != null) {
                groupGlMember = queryGroupGlMember(glGroupId, rdmMemberAuditRecord.getGlUserId());
            }
        }
        //更新用户在gitlab组的权限为owner
        updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
    }

    private void updateGitlabGroupMemberWithOwner(Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (groupGlMember == null) {
            // 添加
            addGitlabMember(rdmMemberAuditRecord);
            gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        } else if (!groupGlMember.getAccessLevel().toValue().equals(AccessLevel.OWNER.toValue())) {
            // 更新
            gitlabGroupFixApi.updateMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        }
    }

    private Member queryGroupGlMember(Integer glGroupId, Integer glUserId) {
        return gitlabGroupFixApi.getMember(glGroupId, glUserId);
    }

    private void addGitlabMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        //删除完组的权限后，要把项目层的已同步成功的挨个加上
        addProjectPermission(rdmMemberAuditRecord);
    }


    private void addProjectPermission(RdmMemberAuditRecord rdmMemberAuditRecord) {
        List<RdmMember> rdmMembers = queryRdmMembers(rdmMemberAuditRecord);
        if (!CollectionUtils.isEmpty(rdmMembers)) {
            rdmMembers.forEach(rdmMember1 -> {
                gitlabProjectFixApi.addMember(rdmMember1.getGlProjectId(), rdmMember1.getGlUserId(), rdmMember1.getGlAccessLevel(), rdmMember1.getGlExpiresAt());
            });
        }
    }


    private List<RdmMember> queryRdmMembers(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember projectRdmMember = new RdmMember();
        projectRdmMember.setType(AuthorityTypeEnum.PROJECT.getValue());
        projectRdmMember.setProjectId(rdmMemberAuditRecord.getProjectId());
        projectRdmMember.setUserId(rdmMemberAuditRecord.getUserId());
        projectRdmMember.setSyncGitlabFlag(Boolean.TRUE);
        return rdmMemberRepository.select(projectRdmMember);
    }
}
