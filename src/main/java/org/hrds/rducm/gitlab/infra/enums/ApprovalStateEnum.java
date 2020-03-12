package org.hrds.rducm.gitlab.infra.enums;

/**
 * 审批状态枚举
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
public enum ApprovalStateEnum {
    /**
     * 待审批
     */
    PENDING("PENDING"),
    /**
     * 审批通过
     */
    APPROVED("APPROVED"),
    /**
     * 审批拒绝
     */
    REJECTED("REJECTED");

    private String code;

    ApprovalStateEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public ApprovalStateEnum setCode(String code) {
        this.code = code;
        return this;
    }
}
