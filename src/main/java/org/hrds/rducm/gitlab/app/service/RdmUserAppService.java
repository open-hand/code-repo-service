package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.RdmUserViewDTO;

/**
 * @author xy
 */
public interface RdmUserAppService {
    RdmUserViewDTO queryUserSelf();

    RdmUserViewDTO queryUser(Long userId);

    void createUserWithRandomPassword(Long userId, String glEmail, String glUsername, String glName);

//    void updatePasswordForUser(String password, String confirmPassword);
}
