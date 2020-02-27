package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Date;
import java.util.List;

public interface GitlabMemberRepository extends BaseRepository<GitlabMember> {
    /**
     * 根据唯一索引查询成员
     * @param repositoryId
     * @param userId
     * @return
     */
    GitlabMember selectOneByUk(Long repositoryId, Long userId);

    List<Member> queryMembersFromGitlab(Integer glProjectId);

    void batchAddOrUpdateMembersToGitlab(List<GitlabMember> gitlabMembers);

    void insertMemberBefore(GitlabMember param);

    /**
     * 更新成员, 预更新(同步标识设为false, gitlab字段置空)
     * 执行成功会设置主键和版本号
     *
     * @param gitlabMember
     */
    void updateMemberBefore(GitlabMember gitlabMember);

    /**
     * 批量新增或更新成员, 预更新(同步标识设为false, gitlab字段置空)
     * 执行成功后会设置主键和版本号
     *
     * @param gitlabMembers
     */
    void batchAddOrUpdateMembersBefore(List<GitlabMember> gitlabMembers);

    void updateMemberToGitlab(GitlabMember param);

    void removeMemberToGitlab(Integer glProjectId, Integer glUserId);

    void checkIsSyncGitlab(GitlabMember m);
}
