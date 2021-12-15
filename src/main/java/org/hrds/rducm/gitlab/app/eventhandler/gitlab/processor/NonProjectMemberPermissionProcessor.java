package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import java.util.List;
import java.util.Objects;
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
public class NonProjectMemberPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;


    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;


    @Override
    public void repairProjectPermissionByRole(Member projectGlMember, Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (Objects.isNull(rdmMember)) {
            // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
            handDbMemberIsNull(projectGlMember, groupGlMember, rdmMemberAuditRecord);
        } else {
            handDbMemberExist(projectGlMember, groupGlMember, rdmMember, rdmMemberAuditRecord);
        }
    }

    private void handDbMemberExist(Member projectGlMember, Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (projectGlMember != null) {
            syncToDb(projectGlMember, rdmMember);
            //同步成功的 组里面没有角色 gitlab的AccessLevel只可能小于50  就按照choerodon来修数据 跟新时必须确保成员的权限小于owner
            handgProjectMemberExist(projectGlMember, groupGlMember, rdmMember, rdmMemberAuditRecord);
        } else {
            handgProjectMemberNotExist(groupGlMember, rdmMember, rdmMemberAuditRecord);
        }
    }

    private void handgProjectMemberNotExist(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (rdmMember.getSyncGitlabFlag() && rdmMember.getGlAccessLevel() < 50) {
            //在添加权限之前需要判断组的权限有没有
            RdmMember groupMember = getRdmMember(rdmMemberAuditRecord);
            if (groupMember != null) {
                gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                addProjectPermission(rdmMemberAuditRecord);
            } else {
                gitlabProjectFixApi.addMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
            }
        } else {
            rdmMemberRepository.deleteByPrimaryKey(rdmMember);
        }
    }

    private void handgProjectMemberExist(Member projectGlMember, Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (!Objects.isNull(rdmMember.getGlAccessLevel()) && rdmMember.getGlAccessLevel() < 50 && projectGlMember.getAccessLevel().value.intValue() < 50) {
            //有一些项目对应的组的id和他实际在gitlab上的组的id不一致，这里跟新会400
            //在添加权限之前需要判断组的权限有没有
            RdmMember member = getRdmMember(rdmMemberAuditRecord);
            if (member != null) {
                if (groupGlMember != null) {
                    gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                    addProjectPermission(rdmMemberAuditRecord);
                    return;
                }
            }
            gitlabProjectFixApi.updateMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
        } else {
            rdmMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
            rdmMemberRepository.updateByPrimaryKey(rdmMember);
        }
    }

    private void syncToDb(Member projectGlMember, RdmMember rdmMember) {
        if (!rdmMember.getSyncGitlabFlag() || Objects.isNull(rdmMember.getGlAccessLevel())) {
            rdmMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
            rdmMember.setSyncGitlabFlag(Boolean.TRUE);
            rdmMemberRepository.updateByPrimaryKey(rdmMember);
        }
    }

    private void handDbMemberIsNull(Member projectGlMember, Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (groupGlMember != null) {
            gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
            addProjectPermission(rdmMemberAuditRecord);
            return;
        }
        if (projectGlMember != null) {
            gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
            return;
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
