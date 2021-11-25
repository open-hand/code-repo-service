package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import java.util.Map;
import java.util.Objects;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor.RolePermissionProcessor;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberAuditRecordRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by wangxiang on 2021/8/26
 */
@Component
public class ProjectGitlabPermissionHandler extends AbstractGitlabPermissionHandler {

    private Logger LOGGER = LoggerFactory.getLogger(ProjectGitlabPermissionHandler.class);

    private static final String PERMISSION_PROCESSOR = "PermissionProcessor";

    @Autowired
    private RdmMemberRepository rdmMemberRepository;


    @Autowired
    private GitlabProjectFixApi gitlabProjectFixApi;


    @Autowired
    private RdmMemberAuditRecordRepository rdmMemberAuditRecordRepository;

    @Autowired
    private Map<String, RolePermissionProcessor> permissionProcessorMap;


    @Override
    public void permissionRepair(String role, RdmMemberAuditRecord rdmMemberAuditRecord) {
        //校验 在项目层 如果 glProjectId为null 则直接同步
        if (rdmMemberAuditRecord.getGlProjectId() == null) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            return;
        }
        RdmMember dbRdmMember = getDbRdmMember(rdmMemberAuditRecord);
        if (rdmMemberAuditRecord.getGlUserId() == null && dbRdmMember == null) {
            rdmMemberAuditRecordRepository.updateSyncTrueByPrimaryKeySelective(rdmMemberAuditRecord);
            //如果choerodon为null 直接删除这个账户
            if (rdmMemberAuditRecord.getGlProjectId() != null && rdmMemberAuditRecord.getGlUserId() != null) {
                gitlabProjectFixApi.removeMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
            }
            return;
        }
        Member projectGlMember = null;
        if (rdmMemberAuditRecord.getGlUserId() != null) {
            projectGlMember = queryProjectGlMember(rdmMemberAuditRecord);
        }
        if (projectGlMember == null) {
            LOGGER.warn("project member not exist user id is {}", rdmMemberAuditRecord.getUserId());
        }

        permissionProcessorMap.get(role + PERMISSION_PROCESSOR).repairProjectPermissionByRole(projectGlMember, dbRdmMember, rdmMemberAuditRecord);
    }

    private Member queryProjectGlMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        return gitlabProjectFixApi.getMember(rdmMemberAuditRecord.getGlProjectId(), rdmMemberAuditRecord.getGlUserId());
    }

    @Override
    protected RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        Long repositoryId = getRepositoryId(rdmMemberAuditRecord);
        // 查询权限
        return rdmMemberRepository.selectOneByUk(rdmMemberAuditRecord.getProjectId(), repositoryId, rdmMemberAuditRecord.getUserId());
    }

    private Long getRepositoryId(RdmMemberAuditRecord rdmMemberAuditRecord) {
        return (Objects.isNull(rdmMemberAuditRecord.getRepositoryId()) || rdmMemberAuditRecord.getRepositoryId() == 0) ? 0 : rdmMemberAuditRecord.getRepositoryId();
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
