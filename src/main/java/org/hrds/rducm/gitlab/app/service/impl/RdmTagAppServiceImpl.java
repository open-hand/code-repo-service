package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.api.controller.dto.tag.ProtectedTagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagQueryDTO;
import org.hrds.rducm.gitlab.app.service.RdmTagAppService;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmTagRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmTagService;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabRepositoryApi;
import org.hrds.rducm.gitlab.infra.client.gitlab.api.GitlabTagsApi;
import org.hrds.rducm.gitlab.infra.util.AssertExtensionUtils;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RdmTagAppServiceImpl implements RdmTagAppService {
    @Autowired
    private RdmTagRepository rdmTagRepository;
    @Autowired
    private RdmRepositoryRepository repositoryRepository;
    @Autowired
    private IRdmTagService iRdmTagService;
    @Autowired
    private GitlabRepositoryApi gitlabRepositoryApi;
    @Autowired
    private GitlabTagsApi gitlabTagsApi;

    @Override
    public List<TagDTO> getTags(Long repositoryId, TagQueryDTO tagQueryDTO) {
        // 参数处理
        if (Optional.ofNullable(tagQueryDTO.getExcludeProtectedFlag()).orElse(false)) {
            return iRdmTagService.getTagsWithExcludeProtected(repositoryId);
        }

        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);
        List<Tag> tags = rdmTagRepository.getTagsFromGitlab(rdmRepository.getGlProjectId());
        return ConvertUtils.convertList(tags, TagDTO.class);
    }

    @Override
    public List<ProtectedTagDTO> getProtectedTags(Long repositoryId) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);
        List<ProtectedTag> protectedTags = rdmTagRepository.getProtectedTagsFromGitlab(rdmRepository.getGlProjectId());

        return ConvertUtils.convertList(protectedTags, ProtectedTagDTO.class)
                .stream()
                .sorted(Comparator.comparing(ProtectedTagDTO::getName))
                .collect(Collectors.toList());
    }

    @Override
    public ProtectedTagDTO protectTag(Long repositoryId,
                                      String tagName,
                                      Integer createAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);

        // 校验标记是否已被保护
        ProtectedTag glProtectedTag = gitlabTagsApi.getProtectedTag(rdmRepository.getGlProjectId(), tagName);
        AssertExtensionUtils.isNull(glProtectedTag, "error.protected.tag.exist");

        ProtectedTag protectedTag = rdmTagRepository.protectTag(rdmRepository.getGlProjectId(), tagName, createAccessLevel);
        return ConvertUtils.convertObject(protectedTag, ProtectedTagDTO.class);
    }

    @Override
    public ProtectedTagDTO updateProtectedTag(Long repositoryId,
                                              String tagName,
                                              Integer createAccessLevel) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);

        // 由于Gitlab不提供修改保护标签的api, 故只能先删除, 再新增
        rdmTagRepository.unprotectTag(rdmRepository.getGlProjectId(), tagName);
        ProtectedTag protectedTag = rdmTagRepository.protectTag(rdmRepository.getGlProjectId(), tagName, createAccessLevel);
        return ConvertUtils.convertObject(protectedTag, ProtectedTagDTO.class);
    }

    @Override
    public void unprotectTag(Long repositoryId, String tagName) {
        // 获取对应Gitlab项目id todo 临时
        RdmRepository rdmRepository = repositoryRepository.selectByUk(repositoryId);
        rdmTagRepository.unprotectTag(rdmRepository.getGlProjectId(), tagName);
    }
}
