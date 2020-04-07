package org.hrds.rducm.gitlab.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hrds.rducm.gitlab.domain.aggregate.MemberAuthDetailAgg;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;

import java.util.List;

/**
 * Mapper
 */
public interface RdmMemberMapper extends BaseMapper<RdmMember> {
    /**
     * 查询成员已授权服务数
     *
     * @param organizationId
     * @param projectId
     * @return
     */
    List<MemberAuthDetailAgg> selectMembersRepositoryAuthorized(@Param("organizationId") Long organizationId,
                                                                @Param("projectId") Long projectId);
}
