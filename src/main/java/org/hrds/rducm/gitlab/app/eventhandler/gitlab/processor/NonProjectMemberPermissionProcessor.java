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
public class NonProjectMemberPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;

    /**
     * 修复组层级的非项目成员的权限
     *
     * @param groupGlMember
     * @param rdmMember
     * @param rdmMemberAuditRecord
     */
    @Override
    public void repairGroupPermissionByRole(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (rdmMember == null) {
            if (groupGlMember != null) {
                gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
            }
        } else {
            if (groupGlMember != null) {
                //如果组的权限是owner，直接删除，因为能手动分给group最高只有MAINTAINER,
                if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
                    //获取项目下有gitlab Owner 标签的用户
                    List<C7nUserVO> gitlabOwners = c7nBaseServiceFacade.listCustomGitlabOwnerLableUser(rdmMemberAuditRecord.getProjectId(), "GITLAB_OWNER");
                    if (CollectionUtils.isEmpty(gitlabOwners)) {
                        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
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
                } else {
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
            } else {
                //组在gitlab上的权限为null，数据库又是同步成功了  则添加group member
                gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
            }

        }
    }

    @Override
    public void repairProjectPermissionByRole(Member projectGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        if (Objects.isNull(rdmMember)) {
            // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
            if (projectGlMember != null) {
                gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
            }
        } else {
            //如果gitlab组的权限为null,
            if (projectGlMember != null) {
                // gitlab项目的权限不为null
                // 2 如果同步失败的用户或者权限小于50,按照gitlab的权限来修复
                //这里为项目变更权限的时候需要注意，如果数据库的用户的权限是50，这里按照gitlab的权限来修复。
                //如果用户是同步失败了的， AccessLevel为null
                if (!rdmMember.getSyncGitlabFlag() || Objects.isNull(rdmMember.getGlAccessLevel())) {
                    rdmMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    rdmMember.setSyncGitlabFlag(Boolean.TRUE);
                    rdmMemberRepository.updateByPrimaryKey(rdmMember);
                }
                //同步成功的 组里面没有角色 gitlab的AccessLevel只可能小于50  就按照choerodon来修数据 跟新时必须确保成员的权限小于owner
                if (!Objects.isNull(rdmMember.getGlAccessLevel()) && rdmMember.getGlAccessLevel() < 50 && projectGlMember.getAccessLevel().value.intValue() < 50) {
                    //有一些项目对应的组的id和他实际在gitlab上的组的id不一致，这里跟新会400
                    //在添加权限之前需要判断组的权限有没有
                    RdmMember groupMember = queryRdmMember(rdmMemberAuditRecord);
                    if (groupMember != null) {
                        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                        //删除完组的权限后，要把项目层的已同步成功的挨个加上
                        List<RdmMember> rdmMembers = queryRdmMembers(rdmMemberAuditRecord);
                        addProjectMembers(rdmMembers);
                        gitlabGroupFixApi.addMember(groupMember.getgGroupId(), groupMember.getGlUserId(), groupMember.getGlAccessLevel(), groupMember.getGlExpiresAt());
                    } else {
                        gitlabProjectFixApi.updateMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
                    }
                } else {
                    rdmMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    rdmMemberRepository.updateByPrimaryKey(rdmMember);
                }

            } else {
                // 如果gitlab组的权限为null,gitlab项目的权限也为null
                // 如果在choerodon是同步成功的 权限小于50，则按照choerodon来修复权限
                if (rdmMember.getSyncGitlabFlag() && rdmMember.getGlAccessLevel() < 50) {
                    //在添加权限之前需要判断组的权限有没有
                    RdmMember groupMember = queryRdmMember(rdmMemberAuditRecord);
                    if (groupMember != null) {
                        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                        //删除完组的权限后，要把项目层的已同步成功的挨个加上
                        List<RdmMember> rdmMembers = queryRdmMembers(rdmMemberAuditRecord);
                        addProjectMembers(rdmMembers);
                        gitlabGroupFixApi.addMember(groupMember.getgGroupId(), groupMember.getGlUserId(), groupMember.getGlAccessLevel(), groupMember.getGlExpiresAt());
                    } else {
                        gitlabProjectFixApi.addMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
                    }
                }
                //如果在gitlab 一个权限也没有，在choerodon又是同步失败的，则直接删除这种数据
                //如果如果gitlab组的权限为null,gitlab项目的权限也为null，同步成功，且权限>50，这种数据也删除
                else {
                    rdmMemberRepository.deleteByPrimaryKey(rdmMember);
                }
            }
        }
    }

    private void addProjectMembers(List<RdmMember> rdmMembers) {
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
