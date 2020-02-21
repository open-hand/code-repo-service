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

    public static final String GITLAB_USER = "GitlabUser";
    public static final String GITLAB_MEMBER = "GitlabMember";


    @Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(GITLAB_USER, "GitlabUser 案例"),
                new Tag(GITLAB_MEMBER, "GitlabMember 案例")
        );
    }
}
