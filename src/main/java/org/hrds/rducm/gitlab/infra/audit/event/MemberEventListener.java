package org.hrds.rducm.gitlab.infra.audit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hrds.rducm.gitlab.domain.facade.C7nBaseServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmOperationLogRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmUserRepository;
import org.hrds.rducm.gitlab.infra.enums.RdmAccessLevel;
import org.hrds.rducm.gitlab.infra.feign.vo.C7nUserVO;
import org.hrds.rducm.gitlab.infra.util.PlaceholderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberEventListener implements ApplicationListener<MemberEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberEventListener.class);

    @Autowired
    private RdmOperationLogRepository operationLogRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RdmUserRepository userRepository;
    @Autowired
    private C7nBaseServiceFacade c7NBaseServiceFacade;

    @Override
    public void onApplicationEvent(MemberEvent event) {
        LOGGER.info("[成员管理]操作审计: 监听到[{}]事件", event.getEventType().getName());

        createOperationLog(event);

        LOGGER.info("[成员管理]操作审计: [{}]事件处理完毕", event.getEventType().getName());
    }

    private void createOperationLog(MemberEvent event) {
        // 获取
        Long userId = DetailsHelper.getUserDetails().getUserId();

        Long organizationId = event.getEventParam().getOrganizationId();
        Long projectId = event.getEventParam().getProjectId();
        Long repositoryId = event.getEventParam().getRepositoryId();

        // 封装额外参数
        String extraParam = buildExtraParamMap(event);

        // 封装模板参数, 用于替换"操作内容"模板
        Map<String, Object> templateMap = buildTemplateMap(event);
        String opContent = event.getEventType().getContent();
        opContent = PlaceholderUtils.format(opContent, templateMap);

        // 插入数据库
        RdmOperationLog operationLog = new RdmOperationLog();
        operationLog.setOpUserId(userId)
                .setOrganizationId(organizationId)
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
        Long userId = DetailsHelper.getUserDetails().getUserId();

        Long projectId = event.getEventParam().getProjectId();
        Long targetUserId = event.getEventParam().getTargetUserId();
        Integer accessLevel = event.getEventParam().getAccessLevel();
        Date expiresAt = event.getEventParam().getExpiresAt();

        String sourceUserIdStr;
        String targetUserIdStr;
        String accessLevelStr;
        String expiresAtStr;
        String opDateStr;

        C7nUserVO c7nUserS = c7NBaseServiceFacade.detailC7nUser(userId);
        C7nUserVO c7nUserT = c7NBaseServiceFacade.detailC7nUser(targetUserId);

        sourceUserIdStr = c7nUserS.getRealName() + "(" + c7nUserS.getLoginName() + ")";
        targetUserIdStr = c7nUserT.getRealName() + "(" + c7nUserT.getLoginName() + ")";
        accessLevelStr = accessLevel == null ? null : RdmAccessLevel.forValue(accessLevel).toDesc();

        expiresAtStr = Optional.ofNullable(expiresAt)
                .map(val -> DateFormatUtils.format(val, "yyyy-MM-dd"))
                .orElse("不过期");
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