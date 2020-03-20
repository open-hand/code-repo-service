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

//    void checkIsSyncGitlab(RdmMember m);

}
