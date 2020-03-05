package org.hrds.rducm.gitlab.infra.client.gitlab.model;


/**
 * This is user's email
 */
public class Email {


    private Long id;
    private String email;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
