package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.api.controller.dto.tag.ProtectedTagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;
import org.hrds.rducm.gitlab.app.service.GitlabTagService;
import org.hrds.rducm.gitlab.domain.entity.GitlabRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.GitlabTagRepository;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
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
    public List<TagDTO> getTags(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        List<Tag> tags = gitlabTagRepository.getTagsFromGitlab(gitlabRepository.getGlProjectId());
        return ConvertUtils.convertList(tags, TagDTO.class);
    }

    @Override
    public List<ProtectedTagDTO> getProtectedTags(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        List<ProtectedTag> protectedTags = gitlabTagRepository.getProtectedTagsFromGitlab(gitlabRepository.getGlProjectId());

        return ConvertUtils.convertList(protectedTags, ProtectedTagDTO.class);
    }

    @Override
    public ProtectedTagDTO protectTag(Long repositoryId,
                                      String tagName,
                                      Integer createAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        ProtectedTag protectedTag = gitlabTagRepository.protectTag(gitlabRepository.getGlProjectId(), tagName, createAccessLevel);
        return ConvertUtils.convertObject(protectedTag, ProtectedTagDTO.class);
    }

    @Override
    public ProtectedTagDTO updateProtectedTag(Long repositoryId,
                                              String tagName,
                                              Integer createAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);

        // 由于Gitlab不提供修改保护标签的api, 故只能先删除, 再新增
        gitlabTagRepository.unprotectTag(gitlabRepository.getGlProjectId(), tagName);
        ProtectedTag protectedTag = gitlabTagRepository.protectTag(gitlabRepository.getGlProjectId(), tagName, createAccessLevel);
        return ConvertUtils.convertObject(protectedTag, ProtectedTagDTO.class);
    }

    @Override
    public void unprotectTag(Long repositoryId, String tagName) {
        // 获取对应Gitlab项目id todo 临时
        GitlabRepository gitlabRepository = repositoryRepository.selectByUk(repositoryId);
        gitlabTagRepository.unprotectTag(gitlabRepository.getGlProjectId(), tagName);
    }
}
