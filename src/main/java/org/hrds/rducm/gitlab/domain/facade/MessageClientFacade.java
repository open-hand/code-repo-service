package org.hrds.rducm.gitlab.domain.facade;

import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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