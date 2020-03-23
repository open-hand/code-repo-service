package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.User;
import org.hrds.rducm.gitlab.domain.entity.RdmUser;
import org.hzero.mybatis.base.BaseRepository;

public interface RdmUserRepository extends BaseRepository<RdmUser> {
    RdmUser selectByUk(Long userId);

    RdmUser selectByUk(Integer glUserId);

    User getUserFromGitlab(Integer userId);

    User getUserFromGitlab(String username);

    User createUserToGitlab(String email, String username, String name, String password);

    User updateUserPasswordToGitlab(Long userId, String password);
}
