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

    public static final String GITLAB_USER = "RdmUser";
    public static final String GITLAB_MEMBER = "RdmMember";


    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(GITLAB_USER, "RdmUser 案例"),
                new Tag(GITLAB_MEMBER, "RdmMember 案例")
        );
    }
}
