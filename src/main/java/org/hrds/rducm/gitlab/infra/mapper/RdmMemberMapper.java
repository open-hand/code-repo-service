package org.hrds.rducm.gitlab.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;

import java.util.List;
import java.util.Set;

/**
 * Mapper
 */
public interface RdmMemberMapper extends BaseMapper<RdmMember> {
    /**
     * 查询成员已授权服务数
     *
     * @param organizationId
     * @param projectId
     * @param userIds
     * @return
     */
    List<MemberAuthDetailAgg> selectMembersRepositoryAuthorized(@Param("organizationId") Long organizationId,
                                                                @Param("projectId") Long projectId,
                                                                @Param("userIds") Set<Long> userIds);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsertCustom(List<RdmMember> list);

    List<RdmMember> selectUserGroupAccessLevel(@Param("userIds") Set<Long> userIds,@Param("projectId") Long projectId);

    List<RdmMember> groupMemberByUserId(@Param("projectId") Long projectId, @Param("userIds") List<Long> userIds);

    List<RdmMember> selectProjectMemberByUserIds(@Param("projectId") Long projectId, @Param("userIds") Set<Long> userIds, @Param("type") String type);

    void deleteByIds(@Param("ids") List<Long> ids);
}
