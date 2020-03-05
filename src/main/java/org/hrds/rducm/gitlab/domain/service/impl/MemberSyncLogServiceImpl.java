package org.hrds.rducm.gitlab.domain.service.impl;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.domain.entity.MemberSyncAuditLog;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.hrds.rducm.gitlab.domain.service.IMemberSyncLogService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabPorjectApi;
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
public class MemberSyncLogServiceImpl implements IMemberSyncLogService {
    @Autowired
    private GitlabMemberRepository memberRepository;

    @Autowired
    private GitlabPorjectApi gitlabPorjectApi;
    @Autowired
    private GitlabRepositoryRepository repositoryRepository;

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

    public List<MemberSyncAuditLog> compareMemberPermissionByRepositoryId(Long repositoryId) {
        // 查询仓库id
        GitlabRepository repository = repositoryRepository.selectByUk(repositoryId);
        Integer glProjectId = repository.getGlProjectId();

        // 查询gitlab所有成员
        List<Member> members = gitlabPorjectApi.getMembers(glProjectId);

        // 查询数据库所有成员
        List<GitlabMember> dbMembers = memberRepository.select(new GitlabMember().setGlProjectId(glProjectId));
        Map<Integer, GitlabMember> dbMemberMap = dbMembers.stream().collect(Collectors.toMap(m -> m.getGlUserId(), m -> m));

        // 比较是否有差异
        List<MemberSyncAuditLog> memberAudits = new ArrayList<>();
        for (Member member : members) {
            boolean isDifferent = false;

            // 查找数据库是否有此成员
            GitlabMember dbMember = dbMemberMap.get(member.getId());

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

    public MemberSyncAuditLog buildMemberAudit(Long projectId,
                                               Long repositoryId,
                                               Integer glProjectId,
                                               Member glMember,
                                               GitlabMember dbMember) {
        MemberSyncAuditLog memberAudit = new MemberSyncAuditLog();

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
