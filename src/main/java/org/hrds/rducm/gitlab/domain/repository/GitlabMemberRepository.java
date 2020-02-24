package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Date;
import java.util.List;

public interface GitlabMemberRepository extends BaseRepository<GitlabMember> {
    void batchAddMembersToGitlab(List<GitlabMember> gitlabMembers);

    void updateMemberToGitlab(Long id, Integer glProjectId, Integer glUserId, Integer glAccessLevel, Date glExpiresAt);

    void removeMemberToGitlab(Integer glProjectId, Integer glUserId);
}
