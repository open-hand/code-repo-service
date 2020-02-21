package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.vo.GitlabUserVO;
import org.hrds.rducm.gitlab.domain.entity.GitlabUser;

/**
 * @author xy
 */
public interface GitlabUserService {
    GitlabUserVO queryUserSelf();

    GitlabUserVO queryUser(Long userId);

    void createUserWithRandomPassword(Long userId, String glEmail, String glUsername, String glName);

//    void updatePasswordForUser(String password, String confirmPassword);
}
