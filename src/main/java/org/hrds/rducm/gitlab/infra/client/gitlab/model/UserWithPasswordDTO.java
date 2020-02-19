package org.hrds.rducm.gitlab.infra.client.gitlab.model;

import org.gitlab4j.api.models.User;

public class UserWithPasswordDTO extends User {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}