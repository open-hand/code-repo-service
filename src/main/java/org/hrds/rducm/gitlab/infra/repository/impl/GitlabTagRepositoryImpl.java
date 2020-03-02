package org.hrds.rducm.gitlab.infra.repository.impl;

import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.domain.repository.GitlabTagRepository;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabRepositoryApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabTagsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GitlabTagRepositoryImpl implements GitlabTagRepository {
    @Autowired
    private GitlabTagsApi gitlabTagsApi;
    @Autowired
    private GitlabRepositoryApi gitlabRepositoryApi;

    @Override
    public List<Tag> getTagsFromGitlab(Integer projectId) {
        return gitlabRepositoryApi.getTags(projectId);
    }

    @Override
    public List<ProtectedTag> getProtectedTagsFromGitlab(Object projectIdOrPath) {
        return gitlabTagsApi.getProtectedTags(projectIdOrPath);
    }

    @Override
    public ProtectedTag protectTag(Object projectIdOrPath, String name, Integer createAccessLevel) {
        AccessLevel createAccessLevelEnum = AccessLevel.forValue(createAccessLevel);
        return gitlabTagsApi.protectTag(projectIdOrPath, name, createAccessLevelEnum);
    }

    @Override
    public void unprotectTag(Object projectIdOrPath, String name) {
        gitlabTagsApi.unprotectTag(projectIdOrPath, name);
    }
}
