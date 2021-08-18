package org.hrds.rducm.gitlab.infra.enums;

/**
 * Created by wangxiang on 2021/8/18
 */
public enum AuthorityTypeEnum {
    PROJECT("project"),
    GROUP("group");

    private String value;

    AuthorityTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
