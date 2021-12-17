package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import java.util.List;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Created by wangxiang on 2021/10/23
 */
@Component
public class ProjectMemberPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;

    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;

    @Autowired
    private RdmMemberRepository rdmMemberRepository;


    /**
     * 修复项目成员在project层级的权限
     *
     * @param projectGlMember
     * @param dbRdmMember
     * @param rdmMemberAuditRecord
     */
    @Override
    public void repairProjectPermissionByRole(Member projectGlMember, Member groupGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (dbRdmMember == null || !dbRdmMember.getSyncGitlabFlag()) {
            handDbMemberIsNull(projectGlMember, groupGlMember, rdmMemberAuditRecord);
            return;
        }
        if (projectGlMember != null) {
            //对照项目层的权限，更新为数据库权限
            handgProjectMemberExist(projectGlMember, groupGlMember, dbRdmMember, rdmMemberAuditRecord);
        } else {
            handgProjectMemberNotExist(groupGlMember, dbRdmMember, rdmMemberAuditRecord);
        }
    }

    private boolean handDbMemberIsNull(Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        // 如果dbMember为null 或者同步失败 移除gitlab权限
        if (groupGlMember != null) {
            removeGroupMemberAndAddProjectMember(rdmMemberAuditRecord);
            return true;
        }
        if (projectGlMember != null) {
            gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
            return true;
        }
        return false;
    }

    private void handgProjectMemberExist(Member projectGlMember, Member groupGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (projectGlMember.getAccessLevel().value != dbRdmMember.getGlAccessLevel()) {
            if (groupGlMember != null) {
                removeGroupMemberAndAddProjectMember(rdmMemberAuditRecord);
                return;
            }
            gitlabProjectFixApi.updateMember(dbRdmMember.getGlProjectId(), dbRdmMember.getGlUserId(), dbRdmMember.getGlAccessLevel(), dbRdmMember.getGlExpiresAt());
        }
    }

    private void handgProjectMemberNotExist(Member groupGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        // 查询这个用户 在全局层有没有同步成功的权限，
        if (groupGlMember != null) {
            removeGroupMemberAndAddProjectMember(rdmMemberAuditRecord);
            return;
        }
        gitlabProjectFixApi.addMember(dbRdmMember.getGlProjectId(), dbRdmMember.getGlUserId(), dbRdmMember.getGlAccessLevel(), dbRdmMember.getGlExpiresAt());

    }

    private void removeGroupMemberAndAddProjectMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
        addProjectPermission(rdmMemberAuditRecord);
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

    private RdmMember getRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember groupRdmMember = new RdmMember();
        groupRdmMember.setProjectId(rdmMemberAuditRecord.getProjectId());
        groupRdmMember.setUserId(rdmMemberAuditRecord.getUserId());
        groupRdmMember.setSyncGitlabFlag(Boolean.TRUE);
        return rdmMemberRepository.selectOne(groupRdmMember);
    }

}
