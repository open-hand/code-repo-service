package org.hrds.rducm.gitlab.infra.enums;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/3
 */
public enum IamRoleCodeEnum {
    /**
     * 项目成员
     */
    PROJECT_MEMBER("project-member"),

    /**
     * 项目所有者
     */
    PROJECT_OWNER("project-admin");

    private final String code;

    IamRoleCodeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
