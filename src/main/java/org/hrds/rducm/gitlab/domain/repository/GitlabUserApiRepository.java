package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.User;

/**
 * Repository
 */
public interface GitlabUserApiRepository {

    User getUser(Integer userId);

    User createUser(String email, String username, String name, String password);

    User updateUserPassword(Long userId, String password);
}
