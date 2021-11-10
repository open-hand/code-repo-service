package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import java.util.Map;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor.RolePermissionProcessor;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabGroupFixApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.model.AccessLevel;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nDevopsProjectVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangxiang on 2021/8/26
 */
@Component
public class GroupGitlabPermissionHandler extends AbstractGitlabPermissionHandler {

    private Logger LOGGER = LoggerFactory.getLogger(GroupGitlabPermissionHandler.class);

    private static final String PERMISSION_PROCESSOR = "PermissionProcessor";

    @Autowired
    private RdmMemberRepository rdmMemberRepository;

    @Autowired
    private GitlabGroupFixApi gitlabGroupFixApi;

    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;


    @Autowired
    private Map<String, RolePermissionProcessor> permissionProcessorMap;

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void permissionRepair(String role, RdmMemberAuditRecord rdmMemberAuditRecord) {
        C7nDevopsProjectVO c7nDevopsProjectVO = c7nDevOpsServiceFacade.detailDevopsProjectById(rdmMemberAuditRecord.getProjectId());
        if (c7nDevopsProjectVO == null) {
            LOGGER.warn("devops project not exist project id is {}", rdmMemberAuditRecord.getProjectId());
            return;
        }
        Integer glGroupId = Math.toIntExact(c7nDevopsProjectVO.getGitlabGroupId());
        Member groupGlMember = queryGroupGlMember(glGroupId, rdmMemberAuditRecord.getGlUserId());
        if (groupGlMember == null) {
            LOGGER.warn("group member not exist user id is {}", rdmMemberAuditRecord.getUserId());
        }
        RdmMember dbRdmMember = getDbRdmMember(rdmMemberAuditRecord);
        permissionProcessorMap.get(role + PERMISSION_PROCESSOR).repairGroupPermissionByRole(groupGlMember, dbRdmMember, rdmMemberAuditRecord);
    }

    @Override
    protected RdmMember getDbRdmMember(RdmMemberAuditRecord rdmMemberAuditRecord) {
        RdmMember record = new RdmMember();
        record.setType(AuthorityTypeEnum.GROUP.getValue());
        record.setgGroupId(rdmMemberAuditRecord.getgGroupId());
        record.setProjectId(rdmMemberAuditRecord.getProjectId());
        record.setUserId(rdmMemberAuditRecord.getUserId());
        return rdmMemberRepository.selectOne(record);
    }

    private Member queryGroupGlMember(Integer glGroupId, Integer glUserId) {
        return gitlabGroupFixApi.getMember(glGroupId, glUserId);
    }



}
