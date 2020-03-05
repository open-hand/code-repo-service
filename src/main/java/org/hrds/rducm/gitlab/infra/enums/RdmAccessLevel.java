package org.hrds.rducm.gitlab.infra.enums;

import java.util.HashMap;
import java.util.Map;

public enum RdmAccessLevel {
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

    RdmAccessLevel(int value) {
        this.value = value;
    }

    private static Map<Integer, RdmAccessLevel> valuesMap = new HashMap<Integer, RdmAccessLevel>(9);

    static {
        for (RdmAccessLevel rdmAccessLevel : RdmAccessLevel.values()) {
            valuesMap.put(rdmAccessLevel.value, rdmAccessLevel);
        }

        // Make sure MAINTAINER is mapped to 40 and not MASTER (MASTER is deprecated)
        valuesMap.put(MAINTAINER.value, MAINTAINER);
    }

    public static RdmAccessLevel forValue(Integer value) {

        RdmAccessLevel level = valuesMap.get(value);
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