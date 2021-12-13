package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangxiang on 2021/10/23
 */
@Component
public class OrganizationAdminPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private RdmMemberAppService rdmMemberAppService;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;


    @Override
    public void repairProjectPermissionByRole(Member member, Member groupGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //如果是组织管理员，在项目层级修复，需要插入dbMember
        rdmMemberAppService.insertGroupMember(rdmMemberAuditRecord);
        //更新用户在gitlab组的权限为owner
        updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
    }

    private void updateGitlabGroupMemberWithOwner(Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (groupGlMember == null) {
            // 添加
            gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        } else if (!groupGlMember.getAccessLevel().toValue().equals(AccessLevel.OWNER.toValue())) {
            // 更新
            gitlabGroupFixApi.updateMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), AccessLevel.OWNER.toValue(), null);
        }
    }
}
