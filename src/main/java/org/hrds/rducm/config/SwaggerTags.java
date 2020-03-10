package org.hrds.rducm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Api 描述配置
 */
@Configuration
public class SwaggerTags {

    public static final String RDM_USER = "RdmUser";
    public static final String RDM_MEMBER = "RdmMember";
    public static final String RDM_BRANCH = "RdmBranch";
    public static final String RDM_TAG = "RdmTag";
    public static final String RDM_OPERATION_LOG = "RdmOperationLog";
    public static final String RDM_REPOSITORY = "RdmRepository";


    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(RDM_USER, "用户"),
                new Tag(RDM_MEMBER, "成员"),
                new Tag(RDM_BRANCH, "分支"),
                new Tag(RDM_TAG, "分支"),
                new Tag(RDM_OPERATION_LOG, "操作日志"),
                new Tag(RDM_REPOSITORY, "代码库")
        );
    }
}
