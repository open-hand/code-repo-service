package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.User;

import java.util.Date;

/**
 * Repository
 */
public interface GitlabProjectApiRepository {
    Member addMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt);

    Member updateMember(Object projectIdOrPath, Integer userId, Integer accessLevel, Date expiresAt);

    void removeMember(Integer projectId, Integer userId);
}
