package org.hrds.rducm.gitlab.infra.audit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.gitlab4j.api.models.AccessLevel;
import org.hrds.rducm.gitlab.domain.entity.GitlabOperationLog;
import org.hrds.rducm.gitlab.domain.entity.GitlabUser;
import org.hrds.rducm.gitlab.domain.repository.GitlabOperationLogRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabUserRepository;
import org.hrds.rducm.gitlab.infra.util.PlaceholderUtils;
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

        // 封装额外参数
        String extraParam = buildExtraParamMap(event);

        // 封装模板参数, 用于替换"操作内容"模板
        Map<String, Object> templateMap = buildTemplateMap(event);
        String opContent = event.getEventType().getContent();
        opContent = PlaceholderUtils.format(opContent, templateMap);

        // 插入数据库
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

    private String buildExtraParamMap(MemberEvent event) {
        // 添加参数
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("targetUserId", event.getEventParam().getTargetUserId());
        paramMap.put("accessLevel", event.getEventParam().getAccessLevel());
        paramMap.put("expiresAt", event.getEventParam().getExpiresAt());

        String extraParam;
        try {
            extraParam = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            throw new CommonException("jackson convert to json error", e);
        }
        return extraParam;
    }

    private Map<String, Object> buildTemplateMap(MemberEvent event) {
        // 暂时写死 todo fixme
//        Long userId = DetailsHelper.getUserDetails().getUserId();
        Long userId = 10002L;
        Long targetUserId = event.getEventParam().getTargetUserId();
        Integer accessLevel = event.getEventParam().getAccessLevel();
        Date expiresAt = event.getEventParam().getExpiresAt();

        String sourceUserIdStr;
        String targetUserIdStr;
        String accessLevelStr;
        String expiresAtStr;
        String opDateStr;

        GitlabUser dbUserS = userRepository.selectOne(new GitlabUser().setUserId(userId));
        GitlabUser dbUserT = userRepository.selectOne(new GitlabUser().setUserId(targetUserId));

        sourceUserIdStr = dbUserS.getGlUserName();
        targetUserIdStr = dbUserT.getGlUserName();
        accessLevelStr = AccessLevel.forValue(accessLevel).name();
        expiresAtStr = Optional.ofNullable(objectMapper.convertValue(expiresAt, String.class)).orElse("不过期");
        opDateStr = Optional.ofNullable(objectMapper.convertValue(new Date(event.getTimestamp()), String.class)).orElseThrow(NullPointerException::new);

        // 添加操作日志, 替换占位符
        Map<String, Object> argumentMap = new HashMap<>(5);
        argumentMap.put("sourceUser", sourceUserIdStr);
        argumentMap.put("targetUser", targetUserIdStr);
        argumentMap.put("accessLevel", accessLevelStr);
        argumentMap.put("expiresAt", expiresAtStr);
        argumentMap.put("opDate", opDateStr);

        return argumentMap;
    }
}