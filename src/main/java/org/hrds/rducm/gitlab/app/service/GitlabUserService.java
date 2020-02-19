package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.vo.GitlabUserVO;

/**
 * @author xy
 */
public interface GitlabUserService {
    GitlabUserVO queryUser(Long userId);

    void createUserWithRandomPassword(String email, String username, String name);

    void updatePasswordForUser(String password, String confirmPassword);
}
