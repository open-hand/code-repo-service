package org.hrds.rducm.gitlab.infra.audit.event;

import java.util.Date;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/2/27
 */
public class MemberEvent extends AbstractOperationEvent {
    /**
     * 操作类型
     */
    private static final OperationType OP_TYPE = OperationType.MEMBER_MANAGEMENT;

    private OperationType opType;
    private EventType eventType;
    private EventParam eventParam;

    public MemberEvent(Object source, EventType eventType, EventParam eventParam) {
        super(source, OP_TYPE, eventType.getCode(), eventParam);
        this.opType = OP_TYPE;
        this.eventType = eventType;
        this.eventParam = eventParam;
    }

    public enum EventType {
        /**
         * 事件类型
         */
        ADD_MEMBER("ADD_MEMBER", "添加成员", "{0}添加了成员{1}的权限, 设置权限为{2}, 过期时间为{3}"),
        UPDATE_MEMBER("UPDATE_MEMBER", "更新成员", "{0}修改了成员{1}的权限, 设置权限为{2}, 过期时间为{3}"),
        REMOVE_MEMBER("REMOVE_MEMBER", "移除成员", "{0}移除了成员{1}");

        private String code;
        private String name;
        private String content;

        EventType(String code, String name, String content) {
            this.code = code;
            this.name = name;
            this.content = content;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getContent() {
            return content;
        }
    }

    public static class EventParam {
        private Long projectId;
        private Long repositoryId;
        private Long targetUserId;
        private Integer accessLevel;
        private Date expiresAt;

        public EventParam(Long projectId, Long repositoryId, Long targetUserId, Integer accessLevel, Date expiresAt) {
            this.projectId = projectId;
            this.repositoryId = repositoryId;
            this.targetUserId = targetUserId;
            this.accessLevel = accessLevel;
            this.expiresAt = expiresAt;
        }

        public Long getProjectId() {
            return projectId;
        }

        public Long getRepositoryId() {
            return repositoryId;
        }

        public Long getTargetUserId() {
            return targetUserId;
        }

        public Integer getAccessLevel() {
            return accessLevel;
        }

        public Date getExpiresAt() {
            return expiresAt;
        }
    }

    @Override
    public OperationType getOpType() {
        return opType;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public EventParam getEventParam() {
        return eventParam;
    }
}
