package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.app.service.GitlabMemberService;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hrds.rducm.gitlab.domain.repository.GitlabMemberRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabProjectApiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GitlabMemberServiceImpl implements GitlabMemberService {
    private final GitlabProjectApiRepository gitlabProjectApiRepository;

    private final GitlabMemberRepository gitlabMemberRepository;

    public GitlabMemberServiceImpl(GitlabProjectApiRepository gitlabProjectApiRepository, GitlabMemberRepository gitlabMemberRepository) {
        this.gitlabProjectApiRepository = gitlabProjectApiRepository;
        this.gitlabMemberRepository = gitlabMemberRepository;
    }

    @Override
    public List<GitlabMember> list(Long projectId) {
        GitlabMember query = new GitlabMember();
        query.setProjectId(projectId);
        return gitlabMemberRepository.select(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddMembers(List<GitlabMember> gitlabMembers) {
        // <1> 数据库添加成员
        gitlabMemberRepository.batchInsertSelective(gitlabMembers);

        // <2> 调用gitlab api添加成员 todo 事务一致性问题
        gitlabMembers.forEach((m) -> {
            Member glMember = gitlabProjectApiRepository.addMember(m.getGlProjectId(), m.getGlUserId(), m.getGlAccessLevel(), m.getGlExpiresAt());
            // 回写数据库
            m.setGlMemberId(glMember.getId());
            m.setGlProjectId(m.getGlProjectId());
            m.setGlUserId(m.getGlUserId());
            m.setGlAccessLevel(glMember.getAccessLevel().toValue());
            m.setGlExpiresAt(glMember.getExpiresAt());
        });

        // <3> 批量回写
        gitlabMemberRepository.batchUpdateByPrimaryKeySelective(gitlabMembers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(GitlabMember gitlabMember) {
        // <1> 数据库更新成员
        gitlabMemberRepository.updateByPrimaryKeySelective(gitlabMember);

        // <2> 调用gitlab api更新成员 todo 事务一致性问题
        Member glMember = gitlabProjectApiRepository.updateMember(gitlabMember.getGlProjectId(), gitlabMember.getGlUserId(), gitlabMember.getGlAccessLevel(), gitlabMember.getGlExpiresAt());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long id, Integer glProjectId, Integer glUserId) {
        // <1> 数据库删除成员
        gitlabMemberRepository.deleteByPrimaryKey(id);

        // <2> 调用gitlab api删除成员 todo 事务一致性问题
        gitlabProjectApiRepository.removeMember(glProjectId, glUserId);
    }
}
