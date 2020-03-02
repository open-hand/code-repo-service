package org.hrds.rducm.gitlab.infra.audit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.gitlab4j.api.models.AccessLevel;
import org.hrds.rducm.gitlab.domain.entity.GitlabOperationLog;
import org.hrds.rducm.gitlab.domain.entity.GitlabUser;
import org.hrds.rducm.gitlab.domain.repository.GitlabOperationLogRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberEventListener implements ApplicationListener<MemberEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberEventListener.class);

    @Autowired
    private GitlabOperationLogRepository operationLogRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GitlabUserRepository userRepository;

    @Override
    public void onApplicationEvent(MemberEvent event) {
        LOGGER.info("[成员管理]操作审计: 监听到[{}]事件", event.getEventType().getName());

        createOperationLog(event);

        LOGGER.info("[成员管理]操作审计: [{}]事件处理完毕", event.getEventType().getName());
    }

    private void createOperationLog(MemberEvent event) {
        // 获取
        // 暂时写死 todo fixme
//        Long userId = DetailsHelper.getUserDetails().getUserId();
        Long userId = 10002L;

        Long projectId = event.getEventParam().getProjectId();
        Long repositoryId = event.getEventParam().getRepositoryId();
        Long targetUserId = event.getEventParam().getTargetUserId();
        Integer accessLevel = event.getEventParam().getAccessLevel();
        Date expiresAt = event.getEventParam().getExpiresAt();

        String sourceUserIdStr;
        String targetUserIdStr;
        String accessLevelStr;
        String expiresAtStr;

        GitlabUser dbUserS = userRepository.selectOne(new GitlabUser().setUserId(userId));
        GitlabUser dbUserT = userRepository.selectOne(new GitlabUser().setUserId(targetUserId));

        sourceUserIdStr = dbUserS.getGlUserName();
        targetUserIdStr = dbUserT.getGlUserName();
        accessLevelStr = AccessLevel.forValue(accessLevel).name();
        expiresAtStr = Optional.ofNullable(objectMapper.convertValue(expiresAt, String.class)).orElse("不过期");

        // 添加操作日志
        String opContent = event.getEventType().getContent();
        opContent = MessageFormat.format(opContent, sourceUserIdStr, targetUserIdStr, accessLevelStr, expiresAtStr);


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("targetUserId", event.getEventParam().getTargetUserId());
        paramMap.put("accessLevel", event.getEventParam().getAccessLevel());
        paramMap.put("expiresAt", event.getEventParam().getExpiresAt());


        String extraParam;
        try {
            extraParam = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            throw new CommonException("jackson convert to json error", e);
        }


        GitlabOperationLog operationLog = new GitlabOperationLog();
        operationLog.setOpUserId(userId)
                .setProjectId(projectId)
                .setRepositoryId(repositoryId)
                .setOpType(event.getOpType().getCode())
                .setOpTarget(String.valueOf(event.getEventParam().getTargetUserId()))
                .setOpDate(new Date(event.getTimestamp()))
                .setOpContent(opContent)
                .setOpEventType(event.getEventType().getCode())
                .setExtraParam(extraParam);

        operationLogRepository.insertSelective(operationLog);
    }
}