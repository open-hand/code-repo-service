package org.hrds.rducm.gitlab.app.job;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TaskParam;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.asgard.schedule.enums.TriggerTypeEnum;

import org.hrds.rducm.gitlab.app.assembler.RdmMemberAssembler;
import org.hrds.rducm.gitlab.app.service.RdmMemberAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.facade.MessageClientFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmMemberRepository;
import org.hzero.mybatis.domian.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理权限到期成员的定时任务
 *
 * @author ying.xie@hand-china.com
 * @date 2020/5/25
 */
@Component
public class ExpiredMembersJob {
    private static final Logger logger = LoggerFactory.getLogger(ExpiredMembersJob.class);

    @Autowired
    private RdmMemberAppService rdmMemberAppService;
    @Autowired
    private RdmMemberRepository rdmMemberRepository;
    @Autowired
    private MessageClientFacade messageClientFacade;
    @Autowired
    private RdmMemberAssembler rdmMemberAssembler;

    @JobTask(maxRetryCount = 3,
            code = "handleExpiredMembers",
            description = "代码库移除过期成员")
    @TimedTask(name = "handleExpiredMembers",
            description = "代码库移除过期成员",
            params = {},
            triggerType = TriggerTypeEnum.CRON_TRIGGER,
            cronExpression = "0 0 2 * * ?")
    public void handleExpiredMembers(Map<String, Object> map) {
        // 移除过期成员
        logger.info("移除过期成员定时任务开始执行");

        rdmMemberAppService.handleExpiredMembers();

        logger.info("移除过期成员定时任务执行完毕");
    }

    @JobTask(maxRetryCount = 3,
            code = "removeExpiredMembers",
            description = "代码库移除过期成员")
    public void removeExpiredMembersJob(Map<String, Object> map) {

        // 移除过期成员
        logger.info("移除过期成员定时任务开始执行");

        rdmMemberAppService.handleExpiredMembers();

        logger.info("移除过期成员定时任务执行完毕");
    }

    /**
     * 过期提醒, 提前3天发送站内信提醒项目管理员
     */
//    @JobTask(maxRetryCount = 3,
//            code = "expiredNotification",
//            description = "代码库权限过期提醒")
//    //params = {@JobParam(name = "days", description = "提前x天通知")})
//    @TimedTask(name = "expiredNotification",
//            description = "代码库权限过期提醒",
//            params = {},
//            triggerType = TriggerTypeEnum.CRON_TRIGGER,
//            cronExpression = "0 0 9 * * ?")
//    private void expiredNotification(Map<String, Object> map) {
//        logger.info("代码库权限过期提醒定时任务开始执行");
//
//        // 获取参数
//        int days = 3;
////        Integer.parseInt((String) map.get("days"));
//
//        // <1> 查询x天后过期的成员
//        Condition condition = new Condition(RdmMember.class);
//        condition.createCriteria().andLessThanOrEqualTo(RdmMember.FIELD_GL_EXPIRES_AT, LocalDate.now().plusDays(days));
//        List<RdmMember> expiredRdmMembers = rdmMemberRepository.selectByCondition(condition);
//
//        // 填充用户信息等
//        rdmMemberAssembler.conversionForExpireMembersJob(expiredRdmMembers);
//
//        // 按项目id分组
//        Map<Long, List<RdmMember>> group = expiredRdmMembers.stream().collect(Collectors.groupingBy(m -> m.getProjectId()));
//
//        group.forEach((projectId, members) -> {
//            Long organizationId = members.get(0).getOrganizationId();
//            // 发送站内信
//            messageClientFacade.sendMemberExpireNotification(organizationId, projectId, members);
//        });
//
//        logger.info("代码库权限过期提醒定时任务执行完毕");
//    }

    /**
     * 过期提醒, 提前X天发送站内信提醒项目管理员
     */
    @JobTask(maxRetryCount = 3,
            code = "memberExpiredNotice",
            description = "代码库权限过期提醒",
            params = {@JobParam(name = "days", description = "提前x天通知")})
    public void memberExpiredNoticeNew(Map<String, Object> map) {
        logger.info("代码库权限过期提醒定时任务开始执行");

        // 获取参数
        int days = Integer.parseInt((String) map.get("days"));
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>days:{}", days);

        // <1> 查询x天后过期的成员,并且是未过期的成员
        Condition condition = new Condition(RdmMember.class);
        condition.createCriteria().andLessThanOrEqualTo(RdmMember.FIELD_GL_EXPIRES_AT, LocalDate.now().plusDays(days))
                .andGreaterThanOrEqualTo(RdmMember.FIELD_GL_EXPIRES_AT, LocalDate.now());;
        List<RdmMember> expiredRdmMembers = rdmMemberRepository.selectByCondition(condition);

        // 填充用户信息等
        rdmMemberAssembler.conversionForExpireMembersJob(expiredRdmMembers);

        // 按项目id分组
        Map<Long, List<RdmMember>> group = expiredRdmMembers.stream().collect(Collectors.groupingBy(m -> m.getProjectId()));

        group.forEach((projectId, members) -> {
            Long organizationId = members.get(0).getOrganizationId();
            // 发送站内信
            messageClientFacade.sendMemberExpireNotification(organizationId, projectId, members);
        });

        logger.info("代码库权限过期提醒任务执行完毕");
    }
}
