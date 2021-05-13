package org.hrds.rducm.gitlab.domain.facade.impl;

import io.choerodon.core.oauth.DetailsHelper;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberApplicant;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.C7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.facade.MessageClientFacade;
import org.hrds.rducm.gitlab.infra.enums.IamRoleCodeEnum;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nAppServiceVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nProjectVO;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.mapper.MemberApprovalMapper;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

    /**
     * 代码库权限申请消息
     */
    public static final String RDUCM_MEMBER_APPLICANT = "RDUCM.MEMBER_APPLICANT";
    /**
     * 代码库成员过期提醒消息
     */
    public static final String RDUCM_MEMBER_EXPIRE_NOTICE = "RDUCM.MEMBER_EXPIRE_NOTICE";

    private static final String URL = "/#/rducm/code-lib-management/approve?type=project&id=%s&name=%s&organizationId=%s";
    private static final String APPLY_URL = "/#/rducm/code-lib-management/apply?type=project&id=%s&name=%s&organizationId=%s";

    private static final Logger logger = LoggerFactory.getLogger(MessageClientFacadeImpl.class);

    @Autowired
    private MessageClient messageClient;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;
    @Autowired
    private MemberApprovalMapper memberApprovalMapper;
    @Autowired
    private C7nDevOpsServiceFacade c7nDevOpsServiceFacade;

    @Override
    public void sendApprovalMessage(Long projectId) {
        C7nProjectVO c7nProjectVO = c7NBaseServiceFacade.detailC7nProject(projectId);
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

        args.put("projectName", c7nProjectVO.getName());
        args.put("url", String.format(URL, c7nProjectVO.getId(), c7nProjectVO.getName(), c7nProjectVO.getOrganizationId()));

        logger.info("tenantId:[{}], receivers:[{}]", tenantId, receivers);

        // 异步发送站内消息
        MessageSender messageSender = constructMessageSender(RDUCM_MEMBER_APPLICANT, receivers, null, args, null, projectId);
        messageClient.async().sendMessage(messageSender);
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
                    .append("到期。<br/>");
        });
        args.put("membersInfo", sb.toString());

        logger.info("tenantId:[{}], receivers:[{}]", organizationId, receivers);

        // 异步发送站内消息
        MessageSender messageSender = constructMessageSender(RDUCM_MEMBER_EXPIRE_NOTICE, receivers, null, args, null, projectId);
        messageClient.async().sendMessage(messageSender);
    }


    @Override
    @Async
    public void sendApprovalNotice(Long applicationId,String messageCode) {
        RdmMemberApplicant rdmMemberApplicant = memberApprovalMapper.selectByPrimaryKey(applicationId);
        if (Objects.isNull(rdmMemberApplicant)) {
            logger.info(">>>>Approval information does not exist!>>>>");
            return;
        }
        //您在项目"XXX"下应用服务"XXX"中申请"Reporter（此处需看具体的角色）"角色权限审批已通过。查看详情
        C7nProjectVO c7nProjectVO = c7NBaseServiceFacade.detailC7nProject(rdmMemberApplicant.getProjectId());
        C7nAppServiceVO c7nAppServiceVO = c7nDevOpsServiceFacade.detailC7nAppService(rdmMemberApplicant.getRepositoryId());

        Map<String, String> args = new HashMap<>(16);
        constructParams(rdmMemberApplicant, c7nProjectVO, c7nAppServiceVO, args);

        C7nUserVO c7nUserVO = c7NBaseServiceFacade.detailC7nUser(rdmMemberApplicant.getApplicantUserId());
        List<Receiver> receivers = getReceivers(c7nUserVO);

        MessageSender messageSender = constructMessageSender(messageCode, receivers, null, args, null, c7nProjectVO.getId());
        messageClient.async().sendMessage(messageSender);
    }


    private List<Receiver> getReceivers(C7nUserVO c7nUserVO) {
        List<Receiver> receivers = new ArrayList<>();
        Receiver receiver = new Receiver()
                .setUserId(c7nUserVO.getId())
                .setTargetUserTenantId(c7nUserVO.getOrganizationId());
        receivers.add(receiver);
        return receivers;
    }

    private void constructParams(RdmMemberApplicant rdmMemberApplicant, C7nProjectVO c7nProjectVO, C7nAppServiceVO c7nAppServiceVO, Map<String, String> args) {
        args.put("projectName", c7nProjectVO.getName());
        args.put("appServiceName", c7nAppServiceVO.getName());
        args.put("roleName", RdmAccessLevel.forValue(rdmMemberApplicant.getAccessLevel()).desc);
        args.put("url", String.format(APPLY_URL, c7nProjectVO.getId(), c7nProjectVO.getName(), c7nProjectVO.getOrganizationId()));
    }

    private static MessageSender constructMessageSender(String sendSettingCode, List<Receiver> targetUsers, String receiveType, Map<String, String> params, Map<String, Object> addition, Long projectId) {
        MessageSender messageSender = new MessageSender();
        messageSender.setTenantId(0L);
        messageSender.setReceiverAddressList(targetUsers);
        messageSender.setReceiverTypeCode(receiveType);
        messageSender.setArgs(params);
        messageSender.setMessageCode(sendSettingCode);
        if (addition == null) {
            addition = new HashMap<>();
        }
        addition.putIfAbsent("projectId", Objects.requireNonNull(projectId));
        messageSender.setAdditionalInformation(addition);
        return messageSender;
    }

}