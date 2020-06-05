package org.hrds.rducm.gitlab.domain.facade;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/4
 */
public interface MessageClientFacade {

    /**
     * 申请权限发送站内消息
     *
     * @param projectId
     */
    void sendApprovalMessage(Long projectId);
}