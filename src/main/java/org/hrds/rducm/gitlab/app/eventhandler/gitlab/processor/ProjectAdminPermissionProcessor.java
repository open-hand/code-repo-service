package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import java.util.Objects;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangxiang on 2021/10/23
 */
@Component
public class ProjectAdminPermissionProcessor implements RolePermissionProcessor {

    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;
    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;

    @Override
    public void repairGroupPermissionByRole(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //如果是项目所有者，组成员权限修复
        RdmMember dbRdmMember = getDbRdmMember(rdmMemberAuditRecord);
        if (Objects.isNull(dbRdmMember) && !Objects.isNull(groupGlMember)) {
            rdmMemberAppService.insertGroupMember(rdmMemberAuditRecord);
        }
        //更新用户在Gitlab组的权限为Owmer
        updateGitlabGroupMemberWithOwner(groupGlMember, rdmMemberAuditRecord);
    }

    @Override
    public void repairProjectPermissionByRole(Member member, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //如果是项目所有者，项目权限修复，这里不用关心组的群贤，组的权限在组修复
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

    private Member queryGroupGlMember(Integer glGroupId, Integer glUserId) {
        return gitlabGroupFixApi.getMember(glGroupId, glUserId);
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


    private RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember record = new RdmMember();
        record.setType(AuthorityTypeEnum.GROUP.getValue());
        record.setgGroupId(rdmMemberAuditRecord.getgGroupId());
        record.setProjectId(rdmMemberAuditRecord.getProjectId());
        record.setUserId(rdmMemberAuditRecord.getUserId());
        return rdmMemberRepository.selectOne(record);
    }
}