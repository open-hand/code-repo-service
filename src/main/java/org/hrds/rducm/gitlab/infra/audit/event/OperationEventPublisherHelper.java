package org.hrds.rducm.gitlab.infra.audit.event;

import org.hzero.core.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/2/28
 */

@Component
public class OperationEventPublisherHelper implements ApplicationEventPublisherAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationEventPublisherHelper.class);

    private static ApplicationEventPublisher eventPublisher;

    public static void publishMemberEvent(MemberEvent event) {
        // 校验
        AssertUtils.notNull(event.getEventParam().getProjectId(), "projectId cannot null");
        AssertUtils.notNull(event.getEventParam().getRepositoryId(), "repositoryId cannot null");
        AssertUtils.notNull(event.getEventParam().getTargetUserId(), "targetUserId cannot null");

        Class<?> clazz = event.getSource().getClass();
        LOGGER.info("[成员管理]操作审计: [{}], 发送了[{}]事件", clazz.getSimpleName(), event.getEventType().getName());
        eventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }
}
