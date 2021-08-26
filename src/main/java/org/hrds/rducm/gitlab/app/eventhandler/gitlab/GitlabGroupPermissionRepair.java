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
public class GitlabGroupPermissionRepair extends AbstractGitlabPermissionRepair {

    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;


    @Override
    protected RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember record = new RdmMember();
        record.setType(AuthorityTypeEnum.GROUP.getValue());
        record.setgGroupId(rdmMemberAuditRecord.getgGroupId());
        record.setProjectId(rdmMemberAuditRecord.getProjectId());
        record.setUserId(rdmMemberAuditRecord.getUserId());
        return rdmMemberRepository.selectOne(record);
    }

    @Override
    protected void orgAdminPermissionRepair(RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord, Member groupGlMember, C7nUserVO c7nUserVO) {
        // 修复为group Owner权限
        //如果是组织管理员，又是项目成员，需要插入dbMember
        if (super.isProjectMember(c7nUserVO) && Objects.isNull(rdmMember)) {
            insertGroupMember(rdmMemberAuditRecord);
        }
        super.updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
    }

    @Override
    protected void projectOwnerMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, C7nUserVO c7nUserVO, RdmMember rdmMember, Member groupGlMember) {
        //团队成员 并且是项目所有者
        if (super.isProjectMember(c7nUserVO) && c7nUserVO.isProjectAdmin()) {
            if (Objects.isNull(rdmMember) && !Objects.isNull(groupGlMember)) {
                insertGroupMember(rdmMemberAuditRecord);
            }
            super.updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
        }
    }

    @Override
    protected void projectMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member groupGlMember, C7nUserVO c7nUserVO) {
        if (super.isProjectMember(c7nUserVO) && !c7nUserVO.isProjectAdmin()) {
            //如果是项目成员，数据库choerodon权限为null 或者未同步成功 直接删除gitlab组成员
            if (rdmMember == null || !rdmMember.getSyncGitlabFlag()) {
                // 如果dbMember为null 或者同步失败 移除gitlab权限
                gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
            } else {
                // 如果是项目成员，同步成功 但是权限不匹配 修改gitlab权限
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
                    }
                    //如果组的权限存在，先移除组的权限（随之项目的权限也会被移除,项目原来添加的非Owner权限一并移除）
                    //remove的时候注意  一个组至少存在一个owner, 如果删除返回403则不处理
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
            }
        }
    }

    @Override
    protected void nonProjectMemberPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord, RdmMember rdmMember, Member groupGlMember, C7nUserVO c7nUserVO) {
        if (!super.isProjectMember(c7nUserVO)) {
            //不是团队成员的处理
            if (Objects.isNull(rdmMember)) {
                if (groupGlMember != null) {
                    gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                }
            } else {
                //不是团队成员group member 不为null,dbMember不为null
                if (groupGlMember != null) {
                    //如果是owner 直接删除，手动分配不可能分到owner
                    if (groupGlMember.getAccessLevel().value.intValue() == AccessLevel.OWNER.toValue().intValue()) {
                        gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                        rdmMemberRepository.deleteByPrimaryKey(rdmMember.getId());
                    }
                    gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                    if (rdmMember.getSyncGitlabFlag() && !Objects.isNull(rdmMember.getGlAccessLevel())) {
                        //上一步删除权限可以没有删掉 这里添加可能会报："should be higher than Owner inherited membership from group
                        //如果上一步删除组的权限没有删掉，这里就不给
                        gitlabGroupFixApi.addMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId(), rdmMember.getGlAccessLevel(), rdmMember.getGlExpiresAt());
                    } else {
                        //如果同步失败， 直接删掉这条数据
                        rdmMemberRepository.deleteByPrimaryKey(rdmMember);
                        //如果项目成员的角色存在也直接删掉
                        if (!Objects.isNull(groupGlMember)) {
                            gitlabGroupFixApi.removeMember(rdmMemberAuditRecord.getgGroupId(), rdmMemberAuditRecord.getGlUserId());
                        }
                    }

                } else {
                    // 不是团队成员， group member为null
                    if (rdmMember != null) {
                        rdmMemberRepository.deleteByPrimaryKey(rdmMember.getId());
                    }
                }
            }
        }
    }


    private void insertGroupMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember record = new RdmMember();
        record.setSyncGitlabFlag(true);
        record.setGlAccessLevel(AccessLevel.OWNER.toValue());
        record.setProjectId(rdmMemberAuditRecord.getProjectId());
        record.setUserId(rdmMemberAuditRecord.getUserId());
        record.setOrganizationId(rdmMemberAuditRecord.getOrganizationId());
        record.setGlProjectId(rdmMemberAuditRecord.getGlProjectId());
        record.setGlUserId(rdmMemberAuditRecord.getGlUserId());
        record.setType(AuthorityTypeEnum.GROUP.getValue());
        record.setgGroupId(rdmMemberAuditRecord.getgGroupId());
        rdmMemberRepository.insert(record);
    }


}
