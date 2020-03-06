package org.hrds.rducm.gitlab.api.controller.dto.branch;

/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Greg Messner <greg@messners.com>
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.gitlab4j.api.GitLabApi;

import java.util.HashMap;
import java.util.Map;

@ApiModel
public enum AccessLevelDTO {
    /**
     *
     */
    INVALID(-1),
    //    NONE(0),
    GUEST(10),
    REPORTER(20),
    DEVELOPER(30),
    //    @Deprecated MASTER(40),
    MAINTAINER(40),
    OWNER(50);
//    ADMIN(60);

    @ApiModelProperty("权限值")
    public final Integer value;

    AccessLevelDTO(int value) {
        this.value = value;
    }

    private static Map<Integer, AccessLevelDTO> valuesMap = new HashMap<Integer, AccessLevelDTO>(9);

    static {
        for (AccessLevelDTO accessLevel : AccessLevelDTO.values()) {
            valuesMap.put(accessLevel.value, accessLevel);
        }

        // Make sure MAINTAINER is mapped to 40 and not MASTER (MASTER is deprecated)
        valuesMap.put(MAINTAINER.value, MAINTAINER);
    }

    @JsonCreator
    public static AccessLevelDTO forValue(Integer value) {

        AccessLevelDTO level = valuesMap.get(value);
        if (level != null) {
            return (level);
        }

        GitLabApi.getLogger().warning(String.format("[%d] is not a valid GitLab access level.", value));
        return (value == null ? null : INVALID);
    }

    @JsonValue
    public Integer toValue() {
        return (value);
    }

    @Override
    public String toString() {
        return (value.toString());
    }
}
