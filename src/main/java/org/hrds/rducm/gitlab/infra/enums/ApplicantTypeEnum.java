package org.hrds.rducm.gitlab.infra.enums;

/**
 * 成员申请类型枚举
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/12
 */
public enum ApplicantTypeEnum {
    /**
     * 新成员
     */
    MEMBER_JOIN("MEMBER_JOIN"),
    /**
     * 权限变更
     */
    MEMBER_PERMISSION_CHANGE("MEMBER_PERMISSION_CHANGE");

    private String code;

    ApplicantTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public ApplicantTypeEnum setCode(String code) {
        this.code = code;
        return this;
    }
}
