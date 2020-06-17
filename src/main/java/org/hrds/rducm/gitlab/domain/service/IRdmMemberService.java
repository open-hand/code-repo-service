package org.hrds.rducm.gitlab.domain.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.api.controller.dto.MemberAuthDetailViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.MemberPrivilegeViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RdmMemberViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.RepositoryPrivilegeViewDTO;
import org.hrds.rducm.gitlab.api.controller.dto.base.BaseUserQueryDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 成员管理领域服务类
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
public interface IRdmMemberService {
    /**
     * 查询成员权限占比
     *
     * @param organizationId
     * @param projectId
     * @param pageRequest
     * @param queryDTO
     * @return
     */
    Page<MemberAuthDetailViewDTO> pageMembersRepositoryAuthorized(Long organizationId, Long projectId, PageRequest pageRequest, BaseUserQueryDTO queryDTO);

    /**
     * 查询某个成员的在所有代码库的权限情况
     *
     * @param organizationId
     * @param projectId
     * @param userId
     * @param pageRequest
     * @return
     */
    Page<RdmMemberViewDTO> pageMemberPermissions(Long organizationId,
                                                 Long projectId,
                                                 Long userId,
                                                 PageRequest pageRequest);

    /**
     * 批量新增或更新成员, 预更新(同步标识设为false, gitlab字段置空)
     * 执行成功后会设置主键和版本号
     *
     * @param rdmMembers
     */
    void batchAddOrUpdateMembersBefore(List<RdmMember> rdmMembers);

    /**
     * 新增成员, 预新增(同步标识设为false, gitlab字段置空)
     * 执行成功会设置主键和版本号
     *
     * @param param
     */
    void insertMemberBefore(RdmMember param);

    /**
     * 更新成员, 预更新(同步标识设为false, gitlab字段置空)
     * 执行成功会设置主键和版本号
     *
     * @param param
     */
    void updateMemberBefore(RdmMember param);

    /**
     * 新增或更新成员至gitlab
     *
     * @param param
     * @param isExists 数据库是否存在该成员
     * @return
     */
    Member addOrUpdateMembersToGitlab(RdmMember param, boolean isExists);

    /**
     * 添加成员至gitlab
     *
     * @param glProjectId
     * @param glUserId
     * @param accessLevel
     * @param expiresAt
     * @return
     */
    Member addMemberToGitlab(Integer glProjectId, Integer glUserId, Integer accessLevel, Date expiresAt);

    /**
     * 同步Gitlab成员(整个代码库)
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @return
     */
    int syncAllMembersFromGitlab(Long organizationId, Long projectId, Long repositoryId);

    /**
     * 同步Gitlab成员(单个成员)
     *
     * @param param
     */
    void syncMemberFromGitlab(RdmMember param);

    /**
     * 查询当前用户指定应用服务的代码库权限
     *
     * @param organizationId
     * @param projectId
     * @param repositoryIds
     * @return
     */
    List<MemberPrivilegeViewDTO> selfPrivilege(Long organizationId,
                                               Long projectId,
                                               Set<Long> repositoryIds);

    /**
     * 查询用户拥有权限的应用服务
     *
     * @param organizationId
     * @param projectId
     * @param userIds
     * @return
     */
    List<RepositoryPrivilegeViewDTO> selectRepositoriesByPrivilege(Long organizationId, Long projectId, Set<Long> userIds);

    /**
     * 发送成员操作审计事件
     *
     * @param param
     * @param eventType
     */
    void publishMemberEvent(RdmMember param, MemberEvent.EventType eventType);

    /**
     * 更新成员至gitlab
     *
     * @param param
     * @return
     */
    Member updateMemberToGitlab(RdmMember param);

    /**
     * 移除成员至gitlab
     *
     * @param glProjectId
     * @param glUserId
     */
    void removeMemberToGitlab(Integer glProjectId, Integer glUserId);

    /**
     * 回写数据库
     *
     * @param m
     * @param member
     */
    void updateMemberAfter(RdmMember m, Member member);

    /**
     * 移除成员并新增成员至Gitlab
     * 移除前会先检查Gitlab是否有成员
     *
     * @param glProjectId
     * @param glUserId
     * @param accessLevel
     * @param expiresAt
     * @return
     */
    Member tryRemoveAndAddMemberToGitlab(Integer glProjectId, Integer glUserId, Integer accessLevel, Date expiresAt);

    /**
     * 移除Gitlab成员
     * 移除前会先检查Gitlab是否有成员
     *
     * @param glProjectId
     * @param glUserId
     */
    void tryRemoveMemberToGitlab(Integer glProjectId, Integer glUserId);

    /**
     * 成员过期处理
     *
     * @param expiredRdmMembers 过期成员数据
     */
    void batchExpireMembers(List<RdmMember> expiredRdmMembers);
}
