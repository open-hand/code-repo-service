package org.hrds.rducm.gitlab.domain.facade.impl;

import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.MessageClientFacade;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/4
 */
@Service
public class MessageClientFacadeImpl implements MessageClientFacade {
    public static final String APPLICANT_TEMPLATE_CODE = "RDUCM.MEMBER_APPLICANT.WEB";
    public static final String MEMBER_EXPIRE_NOTICE_TEMPLATE_CODE = "RDUCM.MEMBER_EXPIRE_NOTICE.WEB";


    private static final Logger logger = LoggerFactory.getLogger(MessageClientFacadeImpl.class);

    @Autowired
    private MessageClient messageClient;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    @Override
    public void sendApprovalMessage(Long projectId) {
        // 查询该项目下所有用户
        List<C7nUserVO> c7nUserVOS = c7NBaseServiceFacade.listC7nUsersOnProjectLevel(projectId);
        // 过滤并获取所有"项目管理员"角色的用户
        c7nUserVOS = c7nUserVOS.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> IamRoleCodeEnum.PROJECT_OWNER.getCode().equals(r.getCode())))
                .collect(Collectors.toList());

        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        String lang = "zh_CN";
        List<Receiver> receivers = new ArrayList<>();
        Map<String, String> args = new HashMap<>(16);

        if (CollectionUtils.isEmpty(c7nUserVOS)) {
            // 未找到项目管理员, 不发送站内信
            return;
        }

        c7nUserVOS.forEach(u -> {
            Receiver receiver = new Receiver()
                    .setUserId(u.getId())
                    .setTargetUserTenantId(tenantId);
            receivers.add(receiver);
        });

        logger.info("tenantId:[{}], receivers:[{}]", tenantId, receivers);

        // 异步发送站内消息
        messageClient.async().sendWebMessage(tenantId, APPLICANT_TEMPLATE_CODE, lang, receivers, args);
    }


    @Override
    public void sendMemberExpireNotification(Long organizationId, Long projectId, List<RdmMember> members) {
        // 查询该项目下所有用户
        List<C7nUserVO> c7nUserVOS = c7NBaseServiceFacade.listC7nUsersOnProjectLevel(projectId);
        // 过滤并获取所有"项目管理员"角色的用户
        c7nUserVOS = c7nUserVOS.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> IamRoleCodeEnum.PROJECT_OWNER.getCode().equals(r.getCode())))
                .collect(Collectors.toList());


        String lang = "zh_CN";
        List<Receiver> receivers = new ArrayList<>();
        Map<String, String> args = new HashMap<>(16);

        if (CollectionUtils.isEmpty(c7nUserVOS)) {
            // 未找到项目管理员, 不发送站内信
            return;
        }

        c7nUserVOS.forEach(u -> {
            Receiver receiver = new Receiver()
                    .setUserId(u.getId())
                    .setTargetUserTenantId(organizationId);
            receivers.add(receiver);
        });

        StringBuilder sb = new StringBuilder();
        members.forEach(m -> {
            String projectName = m.getProject().getProjectName();
            String repositoryName = m.getRepository().getRepositoryName();
            String realName = m.getUser().getRealName();
            String expiresAtStr = Optional.ofNullable(m.getGlExpiresAt())
                    .map(val -> DateFormatUtils.format(val, "yyyy-MM-dd"))
                    .orElse("");
            int days = Period.between(LocalDate.now(), m.getGlExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getDays();
            sb.append("[").append(projectName).append("]项目[").append(repositoryName).append("]应用服务").append("[")
                    .append(realName).append("]的权限即将于")
                    .append(expiresAtStr).append("(").append(days).append("天后)")
                    .append("到期<br/>");
        });
        args.put("membersInfo", sb.toString());

        logger.info("tenantId:[{}], receivers:[{}]", organizationId, receivers);

        // 异步发送站内消息
        messageClient.async().sendWebMessage(organizationId, MEMBER_EXPIRE_NOTICE_TEMPLATE_CODE, lang, receivers, args);
    }


}