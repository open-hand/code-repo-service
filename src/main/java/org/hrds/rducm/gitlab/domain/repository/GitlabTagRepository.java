package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.ProtectedTag;
import org.hrds.rducm.gitlab.domain.entity.GitlabMember;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Date;
import java.util.List;

public interface GitlabTagRepository {

    List<ProtectedTag> getProtectedTagsFromGitlab(Object projectIdOrPath);

    ProtectedTag protectTag(Object projectIdOrPath, String name, Integer createAccessLevel);

    void unprotectTag(Object projectIdOrPath, String name);
}
