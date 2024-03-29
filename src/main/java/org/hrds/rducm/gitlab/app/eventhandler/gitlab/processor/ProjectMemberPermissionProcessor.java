package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
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

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;


    @Override
    public void repairGroupPermissionByRole(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (rdmMember == null || !rdmMember.getSyncGitlabFlag()) {
            // 如果dbMember为null 或者同步失败 移除gitlab权限
            gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
            return;
        }
        //如果数据库的权限是同步好了的，那么就处理gitlabGroup的权限
        if (groupGlMember != null) {
            handGroupGlMemberExist(groupGlMember, rdmMember, rdmMemberAuditRecord);
        } else {
            //组在gitlab上的权限为null，数据库又是同步成功了  则添加group member
            gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
        }
    }

    private void handGroupGlMemberExist(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //如果组的权限是owner，直接删除，因为能手动分给group最高只有MAINTAINER,
        if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
            //获取项目下有gitlab Owner 标签的用户(自定义角色可能有Owner标签)
            removeGroupMember(rdmMember, rdmMemberAuditRecord);
        } else {
            //如果组的权限存在并且不是Owner，先移除组的权限（随之项目的权限也会被移除,项目原来添加的非Owner权限一并移除）
            //remove的时候注意  一个组至少存在一个owner, 如果删除返回403则不处理
            removeAndAddGroupMember(rdmMember, rdmMemberAuditRecord);
        }
    }

    private void removeAndAddGroupMember(RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
        //然后如果同步成功，按照choerodon来修复，并且choerodon中为其赋予了权限并且同步成功了
        if (rdmMember.getSyncGitlabFlag() && !Objects.isNull(rdmMember.getGlAccessLevel())) {
            //上一步删除权限可以没有删掉 这里添加可能会报："should be higher than Owner inherited membership from group
            gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
        } else {
            //如果同步失败， 直接删掉这条数据
            rdmMemberRepository.deleteByPrimaryKey(rdmMember);
        }
    }

    private void removeGroupMember(RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        List<C7nUserVO> gitlabOwners = c7nBaseServiceFacade.listCustomGitlabOwnerLableUser(rdmMemberAuditRecord.getProjectId(), "GITLAB_OWNER");
        if (CollectionUtils.isEmpty(gitlabOwners)) {
            gitlabGroupFixApi.removeMember(Objects.requireNonNull(rdmMemberAuditRecord.getgGroupId()), rdmMemberAuditRecord.getGlUserId());
            rdmMemberRepository.deleteByPrimaryKey(rdmMember.getId());
        } else {
            if (gitlabOwners.stream().map(C7nUserVO::getId).collect(Collectors.toList()).contains(rdmMemberAuditRecord.getUserId())) {
                rdmMember.setSyncGitlabFlag(Boolean.TRUE);
                rdmMember.setGlAccessLevel(AccessLevel.OWNER.toValue());
                rdmMemberRepository.updateByPrimaryKey(rdmMember);
            } else {
                gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                rdmMemberRepository.deleteByPrimaryKey(rdmMember.getId());
            }
        }
    }

    /**
     * 修复项目成员在project层级的权限
     *
     * @param projectGlMember
     * @param dbRdmMember
     * @param rdmMemberAuditRecord
     */
    @Override
    public void repairProjectPermissionByRole(Member projectGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //这里只考虑项目层级的权限，组层级的在组层级上修复
        if (dbRdmMember == null || !dbRdmMember.getSyncGitlabFlag()) {
            // 如果dbMember为null 或者同步失败 移除gitlab权限
            gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
            return;
        }
        if (projectGlMember != null) {
            //对照项目层的权限，更新为数据库权限
            updateProjectMember(projectGlMember, dbRdmMember, rdmMemberAuditRecord);

        } else {
            // 查询这个用户 在全局层有没有同步成功的权限，如果有  先删除，再添加
            addProjectMember(dbRdmMember, rdmMemberAuditRecord);
        }
    }

    private void addProjectMember(RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember rdmMember = queryRdmMember(rdmMemberAuditRecord);
        if (rdmMember != null) {
            removeAndAddGitlabMember(rdmMemberAuditRecord, rdmMember);
        } else {
            gitlabProjectFixApi.addMember(dbRdmMember.getGlProjectId(), dbRdmMember.getGlUserId(), dbRdmMember.getGlAccessLevel(), dbRdmMember.getGlExpiresAt());
        }
    }

    private void updateProjectMember(Member projectGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (projectGlMember.getAccessLevel().value != dbRdmMember.getGlAccessLevel()) {
            RdmMember rdmMember = queryRdmMember(rdmMemberAuditRecord);
            if (rdmMember != null) {
                removeAndAddGitlabMember(rdmMemberAuditRecord, rdmMember);
            } else {
                gitlabProjectFixApi.updateMember(dbRdmMember.getGlProjectId(), dbRdmMember.getGlUserId(), dbRdmMember.getGlAccessLevel(), dbRdmMember.getGlExpiresAt());
            }
        }
    }

    private void removeAndAddGitlabMember(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember) {
        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
        //删除完组的权限后，要把项目层的已同步成功的挨个加上
        addProjectPermission(rdmMemberAuditRecord);
        gitlabGroupFixApi.addMember(rdmMember.getgGroupId(), rdmMember.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
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

    private RdmMember queryRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember groupRdmMember = new RdmMember();
        groupRdmMember.setType(AuthorityTypeEnum.GROUP.getValue());
        groupRdmMember.setProjectId(rdmMemberAuditRecord.getProjectId());
        groupRdmMember.setUserId(rdmMemberAuditRecord.getUserId());
        groupRdmMember.setgGroupId(rdmMemberAuditRecord.getgGroupId());
        groupRdmMember.setSyncGitlabFlag(Boolean.TRUE);
        return rdmMemberRepository.selectOne(groupRdmMember);
    }

}
