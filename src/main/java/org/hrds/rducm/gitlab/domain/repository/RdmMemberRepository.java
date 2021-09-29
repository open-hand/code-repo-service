package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;
import java.util.Set;

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
     * 删除一个应用服务的权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @return
     */
    int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId);

    /**
     * 删除指定项目下指定用户的代码库权限
     *
     * @param organizationId
     * @param projectId
     * @param userId
     * @return
     */
    int deleteByProjectIdAndUserId(Long organizationId, Long projectId, Long userId);

    /**
     * 删除指定组织下所有项目指定用户的代码库权限
     *
     * @param organizationId
     * @param userId
     * @return
     */
    void deleteByOrganizationIdAndUserId(Long organizationId, Long userId);

    /**
     * 插入Owner权限的成员
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @param userId
     * @param glProjectId
     * @param glUserId
     * @return
     */
    int insertWithOwner(Long organizationId, Long projectId, Long repositoryId, Long userId, Integer glProjectId, Integer glUserId);

    /**
     * 查询某个代码库成员总数
     *
     * @param repositoryId 代码库id
     * @return 成员数量
     */
    int selectCountByRepositoryId(Long repositoryId);

    /**
     * 查询某个代码库权限大于Maintainer的成员总数
     *
     * @param repositoryId 代码库id
     * @return 成员数量
     */
    int selectManagerCountByRepositoryId(Long repositoryId);

    /**
     * 查询成员已授权服务数
     *
     * @param organizationId
     * @param projectId
     * @param userIds
     * @return
     */
    List<MemberAuthDetailAgg> selectMembersRepositoryAuthorized(Long organizationId, Long projectId, Set<Long> userIds);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsertCustom(List<RdmMember> list);

    List<RdmMember> groupMemberByUserId(Long projectId, List<Long> userIds);
}
