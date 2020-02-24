package org.hrds.rducm.gitlab.infra.enums;

import java.util.HashMap;
import java.util.Map;

public enum GitlabAccessLevel {
    /**
     * Gitlab成员的权限等级
     */
    INVALID(-1),
    NONE(0),
    GUEST(10),
    REPORTER(20),
    DEVELOPER(30),
    @Deprecated MASTER(40),
    MAINTAINER(40),
    OWNER(50),
    ADMIN(60);

    public final Integer value;

    GitlabAccessLevel(int value) {
        this.value = value;
    }

    private static Map<Integer, GitlabAccessLevel> valuesMap = new HashMap<Integer, GitlabAccessLevel>(9);

    static {
        for (GitlabAccessLevel gitlabAccessLevel : GitlabAccessLevel.values()) {
            valuesMap.put(gitlabAccessLevel.value, gitlabAccessLevel);
        }

        // Make sure MAINTAINER is mapped to 40 and not MASTER (MASTER is deprecated)
        valuesMap.put(MAINTAINER.value, MAINTAINER);
    }

    public static GitlabAccessLevel forValue(Integer value) {

        GitlabAccessLevel level = valuesMap.get(value);
        if (level != null) {
            return (level);
        }

        return (value == null ? null : INVALID);
    }

    public Integer toValue() {
        return (value);
    }

    @Override
    public String toString() {
        return (value.toString());
    }
}