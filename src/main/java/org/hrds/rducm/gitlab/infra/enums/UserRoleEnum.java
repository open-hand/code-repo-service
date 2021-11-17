package org.hrds.rducm.gitlab.infra.enums;

/**
 * Created by wangxiang on 2021/11/4
 */
public enum UserRoleEnum {

    ORGANIZATION_ADMIN("organizationAdmin"),
    PROJECT_ADMIN("projectAdmin"),
    PROJECT_MEMBER("projectMember"),
    NON_PROJECT_MEMBER("nonProjectMember");
    private String value;

    UserRoleEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
