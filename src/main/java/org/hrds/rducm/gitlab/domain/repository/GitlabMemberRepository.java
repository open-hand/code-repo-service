package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Date;
import java.util.List;

public interface GitlabMemberRepository extends BaseRepository<GitlabMember> {
    List<Member> queryMembersFromGitlab(Integer glProjectId);

    void batchAddMembersToGitlab(List<GitlabMember> gitlabMembers);

    void updateMemberToGitlab(Long id, Integer glProjectId, Integer glUserId, Integer glAccessLevel, Date glExpiresAt);

    void removeMemberToGitlab(Integer glProjectId, Integer glUserId);
}
