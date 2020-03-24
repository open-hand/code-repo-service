package org.hrds.rducm.gitlab.infra.enums;

import java.util.HashMap;
import java.util.Map;

public enum RdmAccessLevel {
    /**
     * Gitlab成员的权限等级
     */
    INVALID(-1, "Invalid"),
    NONE(0, "None"),
    GUEST(10, "Guest"),
    REPORTER(20, "Reporter"),
    DEVELOPER(30, "Developer"),
    @Deprecated MASTER(40, "Master"),
    MAINTAINER(40, "Maintainer"),
    OWNER(50, "Owner"),
    ADMIN(60, "Admin");

    public final Integer value;
    public final String desc;

    RdmAccessLevel(int value, String desc) {
        this.value = value;
        this.desc = desc;
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

    public String toDesc() {
        return (desc);
    }

    @Override
    public String toString() {
        return (value.toString());
    }
}