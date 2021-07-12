package org.hrds.rducm.gitlab.infra.enums;

import javax.xml.bind.annotation.XmlType;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/6/9
 */
public enum RoleLabelEnum {
    /**
     * 项目层角色标签
     */
    TENANT_ROLE_TPL("TENANT_ROLE_TPL"),
    /**
     * 项目层角色标签
     */
    PROJECT_ROLE("PROJECT_ROLE"),

    /**
     * 项目所有者标签
     */
    PROJECT_ADMIN("PROJECT_ADMIN"),

    /**
     * 项目成员标签
     */
    PROJECT_MEMBER("PROJECT_MEMBER"),

    /**
     * gitlab 项目所有者标签
     */
    GITLAB_OWNER("GITLAB_OWNER"),

    /**
     * gitlab 项目成员标签
     */
    GITLAB_DEVELOPER("GITLAB_DEVELOPER"),

    /**
     * 组织层角色标签
     */
    TENANT_ROLE("TENANT_ROLE"),

    /**
     * 组织层 成员标签
     */
    TENANT_MEMBER("TENANT_MEMBER"),

    /**
     * 组织层 管理员标签
     */
    TENANT_ADMIN("TENANT_ADMIN"),

    /**
     * 平台层角色标签
     */
    SITE_MGR("SITE_MGR"),

    /**
     * 不处理的标签
     */
    DEFAULT("DEFAULT");

    private final String value;

    RoleLabelEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
