package org.hrds.rducm.gitlab.domain.service.impl;

import com.google.common.base.Stopwatch;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberSyncAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberSyncAuditLogRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nBaseServiceService;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberSyncLogService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabAdminApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 安全审计
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/3
 */
@Service
public class RdmMemberSyncLogServiceImpl implements IRdmMemberSyncLogService {
    public static final Logger LOGGER = LoggerFactory.getLogger(RdmMemberSyncLogServiceImpl.class);
    @Autowired
    private RdmMemberSyncAuditLogRepository rdmMemberSyncAuditLogRepository;
    @Autowired
    private RdmMemberRepository memberRepository;
    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private GitlabAdminApi gitlabAdminApi;
//    @Autowired
//    private RdmRepositoryRepository repositoryRepository;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;
    @Autowired
    private IC7nBaseServiceService ic7nBaseServiceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCompare(Long organizationId) {
        Stopwatch started = Stopwatch.createStarted();


        // <0> 删除原有数据
        rdmMemberSyncAuditLogRepository.delete(new RdmMemberSyncAuditLog().setOrganizationId(organizationId));

//        // todo test
//        Pager<Project> projectsPageable = gitlabAdminApi.getProjectsPageable();
//        while (projectsPageable.hasNext()) {
//            if (projectsPageable.getCurrentPage() > 50) {
//                break;
//            }
//            List<Project> glProjects = projectsPageable.next();
//
//            glProjects.forEach(glProject -> {
//                compareMemberPermissionByRepositoryId(organizationId, 1L, 1L, glProject.getId());
//            });
//        }

        // <1> 获取组织下所有项目
        Set<Long> projectIds = ic7nBaseServiceService.listProjectIds(organizationId);;

        // <2> 获取项目下所有代码库
        projectIds.forEach(projectId -> {
            try {
                Map<Long, Long> appServiceIdMap = ic7nDevOpsServiceService.listC7nAppServiceIdsMapOnProjectLevel(projectId);

                appServiceIdMap.forEach((repositoryId, glProjectId) -> {
                    // <3> 对比代码库所有成员权限
                    compareMemberPermissionByRepositoryId(organizationId, projectId, repositoryId, Math.toIntExact(glProjectId));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        long elapsed = started.elapsed(TimeUnit.SECONDS);
        System.out.println("执行时长:" + elapsed);
    }

    /**
     * 定时审计gitlab权限和数据库权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void compareMemberPermission(Long projectId) {
        // 获取所有gitlab项目id
        List<Integer> glProjectIds = new ArrayList<>();

        for (Integer glProjectId : glProjectIds) {

        }
    }

    public List<RdmMemberSyncAuditLog> compareMemberPermissionByRepositoryId(Long organizationId,
                                                                             Long projectId,
                                                                             Long repositoryId,
                                                                             Integer glProjectId) {
        // 查询仓库id
//        RdmRepository repository = repositoryRepository.selectByUk(repositoryId);
//        Integer glProjectId = repository.getGlProjectId();

        // 查询gitlab所有成员
        List<Member> members = gitlabAdminApi.getMembers(glProjectId);
        LOGGER.info("{}项目查询到成员数量为:{}", glProjectId, members.size());

        // 查询数据库所有成员
        List<RdmMember> dbMembers = memberRepository.select(new RdmMember().setGlProjectId(glProjectId));
        Map<Integer, RdmMember> dbMemberMap = dbMembers.stream().collect(Collectors.toMap(m -> m.getGlUserId(), m -> m));

        // 比较是否有差异
        List<RdmMemberSyncAuditLog> memberAudits = new ArrayList<>();
        for (Member member : members) {
            boolean isDifferent = false;

            // 查找数据库是否有此成员
            RdmMember dbMember = dbMemberMap.get(member.getId());

            // 移除
            dbMemberMap.remove(member.getId());
            if (dbMember == null) {
                // 数据库未找到该成员, 说明不一致
                isDifferent = true;
            } else {
                if (!Objects.equals(member.getAccessLevel().toValue(), dbMember.getGlAccessLevel())) {
                    // 如果AccessLevel不相等, 说明不一致
                    isDifferent = true;
                }

                if (!Objects.equals(member.getExpiresAt(), dbMember.getGlExpiresAt())) {
                    // 如果ExpiresAt不相等, 说明不一致
                    isDifferent = true;
                }
            }

            if (isDifferent) {
                memberAudits.add(buildMemberAudit(organizationId, projectId, repositoryId, glProjectId, member, dbMember));
            }
        }

        // 如果dbMemberMap还有数据, 说明不一致
        if (!dbMemberMap.isEmpty()) {
            dbMemberMap.forEach((k, v) -> {
                memberAudits.add(buildMemberAudit(organizationId, projectId, repositoryId, glProjectId, null, v));
            });
        }

        // 保存到数据库
        rdmMemberSyncAuditLogRepository.batchInsertSelective(memberAudits);
        return memberAudits;
    }

    public RdmMemberSyncAuditLog buildMemberAudit(Long organizationId,
                                                  Long projectId,
                                                  Long repositoryId,
                                                  Integer glProjectId,
                                                  Member glMember,
                                                  RdmMember dbMember) {
        RdmMemberSyncAuditLog memberAudit = new RdmMemberSyncAuditLog()
                .setOrganizationId(organizationId)
                .setProjectId(projectId)
                .setRepositoryId(repositoryId)
                .setGlProjectId(glProjectId);

        if (glMember != null) {
            memberAudit.setGlUserId(glMember.getId())
//                    .setGlProjectId(glProjectId)
                    .setGlAccessLevel(glMember.getAccessLevel().toValue())
                    .setGlExpiresAt(glMember.getExpiresAt());
        }

        if (dbMember != null) {
            memberAudit.setUserId(dbMember.getUserId())
//                    .setRepositoryId(dbMember.getRepositoryId())
                    .setAccessLevel(dbMember.getGlAccessLevel())
                    .setExpiresAt(dbMember.getGlExpiresAt());
        }

        return memberAudit;
    }
}
