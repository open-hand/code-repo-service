package org.hrds.rducm.gitlab.domain.service;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.infra.audit.event.MemberEvent;

import java.util.List;

/**
 * 成员管理领域服务类
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/5
 */
public interface IRdmMemberService {
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

//    /**
//     * 批量新增或更新成员至gitlab
//     * 成功后:
//     * 1. 回写数据库
//     * 2. 发送操作审计事件
//     *
//     * @param rdmMembers
//     */
//    void batchAddOrUpdateMembersToGitlab(List<RdmMember> rdmMembers);

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
     * @param param
     * @return
     */
    Member addMemberToGitlab(RdmMember param);

    /**
     * 同步Gitlab成员(整个代码库)
     *
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
     * @param param
     */
    void removeMemberToGitlab(RdmMember param);

    /**
     * 回写数据库
     *
     * @param m
     * @param member
     */
    void updateMemberAfter(RdmMember m, Member member);

    /**
     * 成员过期处理
     *
     * @param expiredRdmMembers 过期成员数据
     */
    void batchExpireMembers(List<RdmMember> expiredRdmMembers);
}
