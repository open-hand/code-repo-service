package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hzero.mybatis.base.BaseRepository;

public interface RdmMemberRepository extends BaseRepository<RdmMember> {
    /**
     * 根据唯一索引查询成员
     *
     * @param projectId
     * @param repositoryId
     * @param userId
     * @return
     */
    RdmMember selectOneByUk(Long projectId, Long repositoryId, Long userId);

    /**
     * 查询某个代码库成员总数
     *
     * @param repositoryId 代码库id
     * @return 成员数量
     */
    int selectCountByRepositoryId(Long repositoryId);

    void checkIsSyncGitlab(RdmMember m);

//    List<Member> queryMembersFromGitlab(Integer glProjectId);

//    void batchAddOrUpdateMembersToGitlab(List<RdmMember> rdmMembers);

//    void insertMemberBefore(RdmMember param);
//
//    /**
//     * 更新成员, 预更新(同步标识设为false, gitlab字段置空)
//     * 执行成功会设置主键和版本号
//     *
//     * @param rdmMember
//     */
//    void updateMemberBefore(RdmMember rdmMember);
//
//    void updateMemberAfter(RdmMember m, Member member);

    /**
     * 批量新增或更新成员, 预更新(同步标识设为false, gitlab字段置空)
     * 执行成功后会设置主键和版本号
     *
     * @param rdmMembers
     */
//    void batchAddOrUpdateMembersBefore(List<RdmMember> rdmMembers);

//    void updateMemberToGitlab(RdmMember param);
//
//    void removeMemberToGitlab(RdmMember param);


}
