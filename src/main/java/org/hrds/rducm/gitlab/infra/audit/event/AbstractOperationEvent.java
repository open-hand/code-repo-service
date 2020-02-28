package org.hrds.rducm.gitlab.infra.audit.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/2/27
 */
public abstract class AbstractOperationEvent extends ApplicationEvent {
    /**
     * 操作类型
     */
    private OperationType opType;
    /**
     * 操作事件类型
     */
    private Object eventType;
    /**
     * 报文
     */
    private Object eventParam;

    protected AbstractOperationEvent(Object source, OperationType opType, Object eventType, Object eventParam) {
        super(source);
        this.opType = opType;
        this.eventType = eventType;
        this.eventParam = eventParam;
    }

    /**
     * 操作类型
     */
    public enum OperationType {
        /**
         * 成员管理
         */
        MEMBER_MANAGEMENT("MEMBER_MANAGEMENT"),
        /**
         * 分支管理
         */
        BRANCH_MANAGEMENT("BRANCH_MANAGEMENT");

        private String code;

        OperationType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public OperationType getOpType() {
        return opType;
    }

    public Object getEventType() {
        return eventType;
    }

    public Object getEventParam() {
        return eventParam;
    }
}
