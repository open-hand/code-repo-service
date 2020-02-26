package org.hrds.rducm.gitlab.infra.repository.impl;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.domain.entity.GitlabMemberAudit;
import org.hrds.rducm.gitlab.domain.repository.GitlabMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabPorjectApi;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GitlabMemberRepositoryImpl extends BaseRepositoryImpl<GitlabMember> implements GitlabMemberRepository {
    @Autowired
    private GitlabPorjectApi gitlabPorjectApi;

    @Override
    public List<Member> queryMembersFromGitlab(Integer glProjectId) {
        return gitlabPorjectApi.getMembers(glProjectId);
    }

    @Override
    public void batchAddMembersToGitlab(List<GitlabMember> gitlabMembers) {
        gitlabMembers.forEach((m) -> {
            // <1> 调用gitlab api添加成员
            Member glMember = gitlabPorjectApi.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            // <2> 回写数据库
            m = this.selectByPrimaryKey(m.getId());

            m.setGlProjectId(m.getGlProjectId());
            m.setGlUserId(m.getGlUserId());
            m.setGlAccessLevel(glMember.getAccessLevel().toValue());
            m.setGlExpiresAt(glMember.getExpiresAt());
            m.setIsSyncGitlab(true);
            m.setSyncDateGitlab(new Date());
            this.updateByPrimaryKeySelective(m);
        });
    }

    @Override
    public void updateMemberToGitlab(Long id, Integer glProjectId, Integer glUserId, Integer glAccessLevel, Date glExpiresAt) {
        Member glMember = gitlabPorjectApi.updateMember(glProjectId, glUserId, glAccessLevel, glExpiresAt);

        // <2> 回写数据库
        GitlabMember m = this.selectByPrimaryKey(id);
        m.setIsSyncGitlab(true);
        m.setSyncDateGitlab(new Date());
        m.setGlAccessLevel(glMember.getAccessLevel().toValue());
        m.setGlExpiresAt(glMember.getExpiresAt());
        this.updateByPrimaryKeySelective(m);
    }

    @Override
    public void removeMemberToGitlab(Integer glProjectId, Integer glUserId) {
        gitlabPorjectApi.removeMember(glProjectId, glUserId);
    }

    /**
     * todo
     * 成员权限审计
     * @param
     */
    private void compareMembersWithGitlab(Long glProjectId, List<GitlabMember> dbMembers, List<Member> glMembers) {
        Map<String, GitlabMemberAudit> gitlabMemberAuditMap = new HashMap<>();

        for (GitlabMember dbMember : dbMembers) {
            GitlabMemberAudit gitlabMemberAudit = new GitlabMemberAudit();
            gitlabMemberAudit.setRepositoryId(dbMember.getRepositoryId())
                    .setUserId(dbMember.getUserId())
                    .setAccessLevel(dbMember.getGlAccessLevel())
                    .setExpiresAt(dbMember.getGlExpiresAt());

            gitlabMemberAuditMap.put(dbMember.getGlProjectId() + "-" + dbMember.getGlUserId(), gitlabMemberAudit);
        }

        for (Member glMember : glMembers) {
            String key = glProjectId + "-" + glMember.getId();



        }
    }
}
