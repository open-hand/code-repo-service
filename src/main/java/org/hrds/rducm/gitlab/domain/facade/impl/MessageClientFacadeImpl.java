package org.hrds.rducm.gitlab.domain.facade.impl;

import io.choerodon.core.oauth.DetailsHelper;
import org.hrds.rducm.gitlab.domain.facade.IC7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.MessageClientFacade;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/4
 */
@Service
public class MessageClientFacadeImpl implements MessageClientFacade {
    public static final String APPLICANT_TEMPLATE_CODE = "RDUCM.MEMBER_APPLICANT";

    @Autowired
    private MessageClient messageClient;
    @Autowired
    private IC7nBaseServiceFacade ic7nBaseServiceFacade;

    /**
     * 申请权限发送站内消息
     * 发送给所有项目管理员
     */
    @Override
    public void sendApprovalMessage(Long projectId) {
        List<C7nUserVO> c7nUserVOS = ic7nBaseServiceFacade.listC7nUsersOnProjectLevel(projectId);
        // 获取所有"项目管理员"角色的用户
        c7nUserVOS = c7nUserVOS.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> IamRoleCodeEnum.PROJECT_OWNER.getCode().equals(r.getCode())))
                .collect(Collectors.toList());

        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        String lang = "zh_CN";
        List<Receiver> receivers = new ArrayList<>();;
        Map<String, String> args = new HashMap<>(16);

        c7nUserVOS.forEach(u -> {
            Receiver receiver = new Receiver()
                    .setUserId(u.getId())
                    .setTargetUserTenantId(tenantId);
            receivers.add(receiver);
        });

        args.put("processName", "测试消息");
        args.put("processDescription", "测试消息");
        // 同步发送站内消息
        messageClient.sendWebMessage(tenantId, APPLICANT_TEMPLATE_CODE, lang, receivers, args);
        // 异步发送站内消息
//        messageClient.async().sendWebMessage(tenantId, messageTemplateCode, lang, Collections.singletonList(receiver), args);
    }
}