package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.app.service.GitlabTagService;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitlabTagServiceImpl implements GitlabTagService {
    @Autowired
    private GitlabTagRepository gitlabTagRepository;
    @Autowired
    private GitlabRepositoryRepository repositoryRepository;

    @Override
    public List<Tag> getTags(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return gitlabTagRepository.getTagsFromGitlab(gitlabRepository.getGlProjectId());
    }

    @Override
    public List<ProtectedTag> getProtectedTags(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return gitlabTagRepository.getProtectedTagsFromGitlab(gitlabRepository.getGlProjectId());
    }

    @Override
    public ProtectedTag protectTag(Long repositoryId,
                                   String glTagName,
                                   Integer glCreateAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        return gitlabTagRepository.protectTag(gitlabRepository.getGlProjectId(), glTagName, glCreateAccessLevel);
    }

    @Override
    public void unprotectTag(Long repositoryId, String glTagName) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        gitlabTagRepository.unprotectTag(gitlabRepository.getGlProjectId(), glTagName);
    }
}
