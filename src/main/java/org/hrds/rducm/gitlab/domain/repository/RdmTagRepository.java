package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;

import java.util.List;

public interface RdmTagRepository {

    List<Tag> getTagsFromGitlab(Integer projectId);

    List<ProtectedTag> getProtectedTagsFromGitlab(Object projectIdOrPath);

    ProtectedTag protectTag(Object projectIdOrPath, String name, Integer createAccessLevel);

    void unprotectTag(Object projectIdOrPath, String name);
}
