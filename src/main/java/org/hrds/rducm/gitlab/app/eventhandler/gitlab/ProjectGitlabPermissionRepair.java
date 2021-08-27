package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
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
 * Created by wangxiang on 2021/8/26
 */
@Component
public class ProjectGitlabPermissionRepair extends AbstractGitlabPermissionRepair {


    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;

    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;

    @Override
    protected RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        Long repositoryId = getRepositoryId(rdmMemberAuditRecord);
        // 查询权限
        return rdmMemberRepository.selectOneByUk(rdmMemberAuditRecord.getProjectId(), repositoryId, rdmMemberAuditRecord.getUserId());
    }

    private Long getRepositoryId(RdmMemberAuditRecord rdmMemberAuditRecord) {
        return (Objects.isNull(rdmMemberAuditRecord.getRepositoryId()) || rdmMemberAuditRecord.getRepositoryId() == 0) ? 0 : rdmMemberAuditRecord.getRepositoryId();
    }

    @Override
    protected void orgAdminPermissionRepair(RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord, Member groupGlMember, C7nUserVO c7nUserVO) {
        // 修复为group Owner权限
        //如果是组织管理员，又是项目成员，需要插入dbMember
        if (super.isProjectMember(c7nUserVO) && Objects.isNull(rdmMember)) {
            insertProjectMember(rdmMemberAuditRecord);
        }
        super.updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
    }

    @Override
    protected void projectOwnerMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, C7nUserVO c7nUserVO, RdmMember rdmMember, Member groupGlMember) {
        //是团队成员，并且是项目管理员
        if (super.isProjectMember(c7nUserVO) && c7nUserVO.isProjectAdmin()) {
            if (Objects.isNull(rdmMember) && !Objects.isNull(groupGlMember)) {
                insertProjectMember(rdmMemberAuditRecord);
            }
            super.updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
        }
    }

    @Override
    protected void projectMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member groupGlMember, C7nUserVO c7nUserVO) {
        //是项目成员 但是 不是项目所有者
        if (super.isProjectMember(c7nUserVO) && !c7nUserVO.isProjectAdmin()) {
            //如果不是项目管理员，项目成员的角色
            if (rdmMember == null || !rdmMember.getSyncGitlabFlag()) {
                // 如果dbMember为null 或者同步失败 移除gitlab权限
                gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());

            } else {
                // 修改gitlab权限
                Member projectGlMember = getGitLabProjectMember(rdmMemberAuditRecord);
                updateGitLabPermission(rdmMemberAuditRecord, rdmMember, projectGlMember, groupGlMember);
            }
        }

    }

    private Member getGitLabProjectMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        return gitlabProjectFixApi.getMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
    }

    @Override
    protected void nonProjectMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member groupGlMember, C7nUserVO c7nUserVO) {
        if (!super.isProjectMember(c7nUserVO)) {
            Member projectGlMember = getGitLabProjectMember(rdmMemberAuditRecord);
            if (Objects.isNull(rdmMember)) {
                // 如果不是团队成员,也不是赋予权限的项目外成员 移除gitlab权限
                if (projectGlMember != null) {
                    gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
                }
            } else {
                updateGitLabPermission(rdmMemberAuditRecord, rdmMember, projectGlMember, groupGlMember);
            }
        }
    }

    private void updateGitLabPermission(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member projectGlMember, Member groupGlMember) {
        if (groupGlMember != null) {
            //如果组的权限是owner，直接删除，项目成员不可能有组的owner
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
            }

            //项目层级的权限只能比group的权限更高才能分配
            //然后如果同步成功，按照choerodon来修复，并且choerodon中为其赋予了权限并且同步成功了
            if (rdmMember.getSyncGitlabFlag() && !Objects.isNull(rdmMember.getGlAccessLevel())
                    && rdmMember.getGlAccessLevel() > groupGlMember.getAccessLevel().value) {
                gitlabProjectFixApi.updateMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
            } else {
                //如果同步失败 或者项目层权限没有组的权限高的， 直接删掉这条数据
                rdmMemberRepository.deleteByPrimaryKey(rdmMember);
                //如果项目成员的角色存在也直接删掉
                if (!Objects.isNull(projectGlMember)) {
                    gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
                }
            }
            //如果gitlab组的权限存在，且是同步失败的
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
                    gitlabProjectFixApi.updateMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
                } else {
                    rdmMember.setGlAccessLevel(projectGlMember.getAccessLevel().value);
                    rdmMemberRepository.updateByPrimaryKey(rdmMember);
                }

            } else {
                // 如果gitlab组的权限为null,gitlab项目的权限也为null
                // 如果在choerodon是同步成功的 权限小于50，则按照choerodon来修复权限
                if (rdmMember.getSyncGitlabFlag() && rdmMember.getGlAccessLevel() < 50) {
                    gitlabProjectFixApi.addMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
                }
                //如果在gitlab 一个权限也没有，在choerodon又是同步失败的，则直接删除这种数据
                //如果如果gitlab组的权限为null,gitlab项目的权限也为null，同步成功，且权限>50，这种数据也删除
                else {
                    rdmMemberRepository.deleteByPrimaryKey(rdmMember);
                }
            }
        }
    }

    private void insertProjectMember(RdmMemberAuditRecord dbRecord) {
        Long repositoryId = getRepositoryId(dbRecord);
        RdmMember rdmMember = new RdmMember();
        rdmMember.setSyncGitlabFlag(true);
        rdmMember.setGlAccessLevel(AccessLevel.OWNER.toValue());
        rdmMember.setProjectId(dbRecord.getProjectId());
        rdmMember.setUserId(dbRecord.getUserId());
        rdmMember.setRepositoryId(repositoryId);
        rdmMember.setOrganizationId(dbRecord.getOrganizationId());
        rdmMember.setGlProjectId(dbRecord.getGlProjectId());
        rdmMember.setGlUserId(dbRecord.getGlUserId());
        rdmMember.setType(AuthorityTypeEnum.PROJECT.getValue());
        rdmMemberRepository.insert(rdmMember);
    }
}
