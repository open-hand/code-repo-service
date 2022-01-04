package org.hrds.rducm.gitlab.app.job;

import java.util.*;
import java.util.stream.Collectors;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.infra.enums.AuthorityTypeEnum;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.feign.BaseServiceFeignClient;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nTenantVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.mapper.RdmMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;

/**
 * Created by wangxiang on 2021/9/13
 */
@Component
public class FixMemberTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixMemberTask.class);

    @Autowired
    private C7nBaseServiceFacade c7nBaseServiceFacade;

    @Autowired
    private RdmMemberMapper rdmMemberMapper;

    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;

    @JobTask(maxRetryCount = 3, code = "fixMemberToGroup", description = "修复用户的权限，增加group层级")
    @TimedTask(name = "fixMemberToGroup", description = "修复用户的权限，增加group层级", oneExecution = true,
            repeatCount = 0, repeatInterval = 1, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS, params = {})
    public void fixMemberPermission(Map<String, Object> map) {
        //1.原来所有的owner的权限统统改为group
        List<C7nTenantVO> c7nTenantVOS = c7nBaseServiceFacade.listAllOrgs();
        if (CollectionUtils.isEmpty(c7nTenantVOS)) {
            LOGGER.info("tenant is null");
        }

        LOGGER.info(">>>>>>>>>>>>>>>start fix member permission>>>>>>>>>>>>>>>>>>");
        c7nTenantVOS.forEach(c7nTenantVO -> {
            Set<Long> projectIds = c7nBaseServiceFacade.listProjectIds(c7nTenantVO.getTenantId());
            if (!CollectionUtils.isEmpty(projectIds)) {
                projectIds.forEach(projectId -> {
                    //查询项目下所有有gitlab owner标签的用户
                    List<C7nUserVO> c7nUserVOS = c7nBaseServiceFacade.listCustomGitlabOwnerLableUser(projectId, "GITLAB_OWNER");
                    if (!CollectionUtils.isEmpty(c7nUserVOS)) {
                        //查询项目组的
                        Long appGroupIdByProjectId = c7nDevOpsServiceFacade.getAppGroupIdByProjectId(projectId);
                        c7nUserVOS.forEach(c7nUserVO -> {
                            RdmMember record = new RdmMember();
                            record.setType(AuthorityTypeEnum.PROJECT.getValue());
                            record.setProjectId(projectId);
                            record.setSyncGitlabFlag(true);
                            record.setUserId(c7nUserVO.getId());
                            record.setGlAccessLevel(RdmAccessLevel.OWNER.value);
                            List<RdmMember> rdmMembers = rdmMemberMapper.select(record);
                            if (!CollectionUtils.isEmpty(rdmMembers)) {
                                RdmMember member = rdmMembers.get(0);
                                rdmMemberMapper.delete(record);
                                //插入项目全局的权限
                                RdmMember rdmMember = new RdmMember();
                                rdmMember.setUserId(c7nUserVO.getId());
                                rdmMember.setGlAccessLevel(RdmAccessLevel.OWNER.value);
                                rdmMember.setSyncGitlabFlag(true);
                                rdmMember.setType(AuthorityTypeEnum.GROUP.getValue());

                                rdmMember.setOrganizationId(c7nTenantVO.getTenantId());
                                rdmMember.setGlUserId(member.getGlUserId());
                                rdmMember.setgGroupId(appGroupIdByProjectId.intValue());
                                rdmMember.setProjectId(projectId);
                                rdmMember.setSyncGitlabDate(member.getSyncGitlabDate());
                                rdmMember.setExpiredFlag(member.getExpiredFlag());
                                //不重复插入
                                RdmMember exists = new RdmMember();
                                exists.setProjectId(projectId);
                                exists.setUserId(c7nUserVO.getId());
                                exists.setType(AuthorityTypeEnum.GROUP.getValue());
                                exists.setGlAccessLevel(RdmAccessLevel.OWNER.value);
                                if (CollectionUtils.isEmpty(rdmMemberMapper.select(exists))) {
                                    rdmMemberMapper.insert(rdmMember);
                                }

                            }
                        });
                    }
                });
            }
        });
        LOGGER.info(">>>>>>>>>>>>>>>end fix member permission>>>>>>>>>>>>>>>>>>");
    }


    @JobTask(maxRetryCount = 3, code = "fixMemberPermissionData", description = "用户权限数据修复")
    @TimedTask(name = "fixMemberPermissionData", description = "用户权限数据修复", oneExecution = true,
            repeatCount = 0, repeatInterval = 1, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS, params = {})
    public void fixMemberPermissionData(Map<String, Object> map) {
        //1.原来所有的owner的权限统统改为group
        List<C7nTenantVO> c7nTenantVOS = c7nBaseServiceFacade.listAllOrgs();
        if (CollectionUtils.isEmpty(c7nTenantVOS)) {
            LOGGER.info("tenant is null");
        }

        LOGGER.info(">>>>>>>>>>>>>>>start fix member permission data>>>>>>>>>>>>>>>>>>");
        c7nTenantVOS.forEach(c7nTenantVO -> {
            Set<Long> projectIds = c7nBaseServiceFacade.listProjectIds(c7nTenantVO.getTenantId());
            if (!CollectionUtils.isEmpty(projectIds)) {
                projectIds.forEach(projectId -> {
                    //查询项目下所有有gitlab owner标签的用户
                    List<C7nUserVO> c7nUserVOS = c7nBaseServiceFacade.listCustomGitlabOwnerLableUser(projectId, "GITLAB_OWNER");
                    if (!CollectionUtils.isEmpty(c7nUserVOS)) {
                        //查询项目组的
                        Long appGroupIdByProjectId = c7nDevOpsServiceFacade.getAppGroupIdByProjectId(projectId);
                        c7nUserVOS.forEach(c7nUserVO -> {
                            RdmMember record = new RdmMember();
                            record.setType(AuthorityTypeEnum.GROUP.getValue());
                            record.setProjectId(projectId);
                            record.setUserId(c7nUserVO.getId());
                            List<RdmMember> rdmMembers = rdmMemberMapper.select(record);
                            boolean contains = rdmMembers.stream().map(RdmMember::getGlAccessLevel).collect(Collectors.toList()).contains(50);
                            List<Long> ids = rdmMembers.stream().filter(rdmMember -> rdmMember.getGlAccessLevel() != 50).map(RdmMember::getId).collect(Collectors.toList());
                            if (!contains) {
                                //插入项目全局的权限
                                RdmMember rdmMember = new RdmMember();
                                rdmMember.setUserId(c7nUserVO.getId());
                                rdmMember.setGlAccessLevel(RdmAccessLevel.OWNER.value);
                                rdmMember.setSyncGitlabFlag(true);
                                rdmMember.setType(AuthorityTypeEnum.GROUP.getValue());

                                rdmMember.setOrganizationId(c7nTenantVO.getTenantId());
                                C7nUserVO userVO = c7nBaseServiceFacade.detailC7nUserOnProjectLevel(projectId, c7nUserVO.getId());
                                if (userVO != null) {
                                    rdmMember.setGlUserId(userVO.getGitlabUserId().intValue());
                                }
                                rdmMember.setgGroupId(appGroupIdByProjectId.intValue());
                                rdmMember.setProjectId(projectId);
                                rdmMember.setSyncGitlabDate(new Date());
                                rdmMemberMapper.insert(rdmMember);
                            }
                            if (!org.apache.commons.collections.CollectionUtils.isEmpty(ids)) {
                                rdmMemberMapper.deleteByIds(ids);
                            }
                        });
                    }
                });
            }
        });
        LOGGER.info(">>>>>>>>>>>>>>>end fix member permission data>>>>>>>>>>>>>>>>>>");
    }

}
