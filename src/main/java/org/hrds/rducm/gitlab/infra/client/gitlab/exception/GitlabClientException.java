package org.hrds.rducm.gitlab.infra.client.gitlab.exception;

import io.choerodon.core.exception.CommonException;

/**
 * Gitlab客户端异常类
 *
 * @author ying.xie@hand-china.com
 * @date 2020/3/2
 */
public class GitlabClientException extends CommonException {
    public static final String CODE = "error.gitlab.client";

    public GitlabClientException(Throwable cause, Object... parameters) {
        super(CODE, cause, parameters);
        if (cause.getMessage().equals("404 Not found")) {
            parameters[0] = "资源不存在";
        }
    }
}
