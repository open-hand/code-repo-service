package org.hrds.rducm.gitlab.domain.service.impl;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberSyncAuditLog;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmMemberSyncLogService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabProjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 安全审计
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/3
 */
@Service
public class RdmMemberSyncLogServiceImpl implements IRdmMemberSyncLogService {
    @Autowired
    private RdmMemberRepository memberRepository;

    @Autowired
    private GitlabProjectApi gitlabProjectApi;
    @Autowired
    private RdmRepositoryRepository repositoryRepository;

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

    public List<RdmMemberSyncAuditLog> compareMemberPermissionByRepositoryId(Long repositoryId) {
        // 查询仓库id
        RdmRepository repository = repositoryRepository.selectByUk(repositoryId);
        Integer glProjectId = repository.getGlProjectId();

        // 查询gitlab所有成员
        List<Member> members = gitlabProjectApi.getMembers(glProjectId);

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
                memberAudits.add(buildMemberAudit(null, repositoryId, glProjectId, member, dbMember));
            }
        }

        // 如果dbMemberMap还有数据, 说明不一致
        if (!dbMemberMap.isEmpty()) {
            dbMemberMap.forEach((k, v) -> {
                memberAudits.add(buildMemberAudit(null, repositoryId, glProjectId, null, v));
            });
        }

        // 保存到数据库
        // todo
        return memberAudits;
    }

    public RdmMemberSyncAuditLog buildMemberAudit(Long projectId,
                                                  Long repositoryId,
                                                  Integer glProjectId,
                                                  Member glMember,
                                                  RdmMember dbMember) {
        RdmMemberSyncAuditLog memberAudit = new RdmMemberSyncAuditLog();

        if (glMember != null) {
            memberAudit.setGlUserId(glMember.getId())
                    .setGlProjectId(glProjectId)
                    .setGlAccessLevel(glMember.getAccessLevel().toValue())
                    .setGlExpiresAt(glMember.getExpiresAt());
        }

        if (dbMember != null) {
            memberAudit.setUserId(dbMember.getUserId())
                    .setRepositoryId(dbMember.getRepositoryId())
                    .setAccessLevel(dbMember.getGlAccessLevel())
                    .setExpiresAt(dbMember.getGlExpiresAt());
        }

        return memberAudit;
    }
}
