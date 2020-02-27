package org.hrds.rducm.gitlab.api.controller.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

public class GitlabUserViewDTO {
    private Long id;
    private Long userId;

    public Long getId() {
        return id;
    }

    public GitlabUserViewDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public GitlabUserViewDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
