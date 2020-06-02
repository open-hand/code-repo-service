package org.hrds.rducm.gitlab.infra.enums;

/**
 * 成员状态枚举
 *
 * @author ying.xie@hand-china.com
 * @date 2020/6/1
 */
public enum RdmMemberStateEnum {
    /**
     * blocked
     */
    BLOCKED("blocked");

    private String code;

    RdmMemberStateEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public RdmMemberStateEnum setCode(String code) {
        this.code = code;
        return this;
    }
}
