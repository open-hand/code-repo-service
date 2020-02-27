package org.hrds.rducm.gitlab.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.domain.entity.GitlabMemberAudit;
import org.hrds.rducm.gitlab.domain.repository.GitlabMemberRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabPorjectApi;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
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
    public GitlabMember selectOneByUk(Long repositoryId, Long userId) {
        GitlabMember gitlabMember = new GitlabMember();
        gitlabMember.setRepositoryId(repositoryId);
        gitlabMember.setUserId(userId);
        return this.selectOne(gitlabMember);
    }

    @Override
    public List<Member> queryMembersFromGitlab(Integer glProjectId) {
        return gitlabPorjectApi.getMembers(glProjectId);
    }

    @Override
    public void batchAddOrUpdateMembersToGitlab(List<GitlabMember> gitlabMembers) {
        gitlabMembers.forEach((m) -> {
            // <1> 判断新增或更新
            boolean isExists;
            if (m.get_status().equals(AuditDomain.RecordStatus.create)) {
                isExists = false;
            } else if (m.get_status().equals(AuditDomain.RecordStatus.update)) {
                isExists = true;
            } else {
                throw new IllegalArgumentException("record status is invalid");
            }

            Member glMember;
            if (isExists) {
                // 调用gitlab api更新成员
                glMember = gitlabPorjectApi.updateMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            } else  {
                // 调用gitlab api添加成员
                glMember = gitlabPorjectApi.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            }

            // <2> 回写数据库
            this.updateMemberAfter(m, glMember);
        });
    }


    @Override
    public void batchAddOrUpdateMembersBefore(List<GitlabMember> gitlabMembers) {
        gitlabMembers.forEach(m -> {
            // 判断新增或修改
            GitlabMember dbMember = this.selectOneByUk(m.getRepositoryId(), m.getUserId());
            boolean isExists = dbMember != null;

            // 设置状态供后续判断
            m.set_status(isExists ? AuditDomain.RecordStatus.update : AuditDomain.RecordStatus.create);

            if (isExists) {
                this.checkIsSyncGitlab(dbMember);
                m.setId(dbMember.getId());
                m.setObjectVersionNumber(dbMember.getObjectVersionNumber());
                this.updateMemberBefore(m);
            } else {
                this.insertMemberBefore(m);
            }
        });
    }

    @Override
    public void insertMemberBefore(GitlabMember param) {
        GitlabMember m = ConvertUtils.convertObject(param, GitlabMember.class);
        m.setIsSyncGitlab(false);
        m.setGlAccessLevel(null);
        m.setGlExpiresAt(null);
        this.insertSelective(m);

        param.setId(m.getId());
        param.setObjectVersionNumber(m.getObjectVersionNumber());
    }

    @Override
    public void updateMemberBefore(GitlabMember param) {
        GitlabMember m = ConvertUtils.convertObject(param, GitlabMember.class);
        m.setIsSyncGitlab(false);
        m.setGlAccessLevel(null);
        m.setGlExpiresAt(null);
        this.updateOptional(m, GitlabMember.FIELD_IS_SYNC_GITLAB, GitlabMember.FIELD_GL_ACCESS_LEVEL, GitlabMember.FIELD_GL_EXPIRES_AT);

        param.setId(m.getId());
        param.setObjectVersionNumber(m.getObjectVersionNumber());
    }

    public void updateMemberAfter(GitlabMember m, Member member) {
        // <2> 回写数据库
        String[] fields = new String[] {
                GitlabMember.FIELD_GL_PROJECT_ID,
                GitlabMember.FIELD_GL_USER_ID,
                GitlabMember.FIELD_GL_ACCESS_LEVEL,
                GitlabMember.FIELD_GL_EXPIRES_AT,
                GitlabMember.FIELD_IS_SYNC_GITLAB,
                GitlabMember.FIELD_SYNC_DATE_GITLAB
        };
        m.setGlAccessLevel(member.getAccessLevel().toValue());
        m.setGlExpiresAt(member.getExpiresAt());
        m.setIsSyncGitlab(true);
        m.setSyncDateGitlab(new Date());
        this.updateOptional(m, fields);
    }

    @Override
    public void updateMemberToGitlab(GitlabMember param) {
        Member glMember = gitlabPorjectApi.updateMember(param.getGlProjectId(), param.getGlUserId(), param.getGlAccessLevel(), param.getGlExpiresAt());

        // <2> 回写数据库
        updateMemberAfter(param, glMember);
    }

    @Override
    public void removeMemberToGitlab(Integer glProjectId, Integer glUserId) {
        gitlabPorjectApi.removeMember(glProjectId, glUserId);
    }

    /**
     * 检查当前记录是否处于"预更新"状态
     * @param m
     */
    @Override
    public void checkIsSyncGitlab(GitlabMember m) {
        if (!m.getIsSyncGitlab()) {
            // 当同步标记为false时, 表示上个事务还未结束
            throw new CommonException("error.sync.flag.false");
        }
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
