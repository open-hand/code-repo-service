package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.GitlabUserViewDTO;

/**
 * @author xy
 */
public interface GitlabUserService {
    GitlabUserViewDTO queryUserSelf();

    GitlabUserViewDTO queryUser(Long userId);

    void createUserWithRandomPassword(Long userId, String glEmail, String glUsername, String glName);

//    void updatePasswordForUser(String password, String confirmPassword);
}
