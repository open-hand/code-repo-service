package org.hrds.rducm.gitlab.domain.facade;

import org.hrds.rducm.gitlab.domain.entity.RdmMember;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/4
 */
public interface MessageClientFacade {

    /**
     * 申请权限发送站内消息
     * 发送给所有[项目管理员]
     *
     * @param projectId
     */
    void sendApprovalMessage(Long projectId);

    /**
     * 代码库成员权限到期站内消息提醒
     * 发送给所有[项目管理员]
     *
     * @param organizationId
     * @param projectId
     * @param members
     */
    void sendMemberExpireNotification(Long organizationId, Long projectId, List<RdmMember> members);

    void sendApprovalNotice(Long applicationId,String messageCode);

}