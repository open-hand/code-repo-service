package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import java.util.List;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Created by wangxiang on 2021/10/23
 */
@Component
public class ProjectAdminPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;

    @Override
    public void repairProjectPermissionByRole(Member member, Member groupGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        rdmMemberAppService.insertGroupMember(rdmMemberAuditRecord);
        //更新用户在gitlab组的权限为owner
        updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
    }


    private void updateGitlabGroupMemberWithOwner(Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (groupGlMember == null) {
            // 添加
            addProjectPermission(rdmMemberAuditRecord);
            gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        } else {
            // 更新
            removeAndAddGitlabMember(rdmMemberAuditRecord);
        }
    }


    private void addProjectPermission(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember projectRdmMember = new RdmMember();
        projectRdmMember.setProjectId(rdmMemberAuditRecord.getProjectId());
        projectRdmMember.setUserId(rdmMemberAuditRecord.getUserId());
        projectRdmMember.setSyncGitlabFlag(Boolean.TRUE);
        List<RdmMember> rdmMembers = rdmMemberRepository.select(projectRdmMember);
        if (!CollectionUtils.isEmpty(rdmMembers)) {
            rdmMembers.forEach(rdmMember1 -> {
                if (rdmMember1.getGlProjectId() == null) {
                    return;
                }
                gitlabProjectFixApi.addMember(rdmMember1.getGlProjectId(), rdmMember1.getGlUserId(), rdmMember1.getGlAccessLevel(), rdmMember1.getGlExpiresAt());
            });
        }
    }


    private void removeAndAddGitlabMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
        //删除完组的权限后，要把项目层的已同步成功的挨个加上
        addProjectPermission(rdmMemberAuditRecord);
        gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
    }
}
