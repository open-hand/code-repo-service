package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.RdmUserViewDTO;

/**
 * @author xy
 */
public interface RdmUserAppService {
    /**
     * 查询用户Gitlab的个人信息
     *
     * @return
     */
    RdmUserViewDTO queryUserSelf();

    /**
     * 创建Gitlab用户并生成随机密码 TODO
     *
     * @param userId
     * @param glEmail
     * @param glUsername
     * @param glName
     */
    @Deprecated
    void createUserWithRandomPassword(Long userId, String glEmail, String glUsername, String glName);
}
