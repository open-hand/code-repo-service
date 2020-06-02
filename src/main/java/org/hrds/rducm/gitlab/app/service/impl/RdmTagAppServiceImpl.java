package org.hrds.rducm.gitlab.app.service.impl;

import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.api.controller.dto.tag.ProtectedTagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagQueryDTO;
import org.hrds.rducm.gitlab.app.service.RdmTagAppService;
import org.hrds.rducm.gitlab.domain.facade.IC7nDevOpsServiceFacade;
import org.hrds.rducm.gitlab.domain.repository.RdmTagRepository;
import org.hrds.rducm.gitlab.domain.service.IRdmTagService;
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
    private IRdmTagService iRdmTagService;
    @Autowired
    private GitlabTagsApi gitlabTagsApi;
    @Autowired
    private IC7nDevOpsServiceFacade ic7NDevOpsServiceFacade;

    @Override
    public List<TagDTO> getTags(Long projectId, Long repositoryId, TagQueryDTO tagQueryDTO) {
        // 参数处理
        if (Optional.ofNullable(tagQueryDTO.getExcludeProtectedFlag()).orElse(false)) {
            return iRdmTagService.getTagsWithExcludeProtected(projectId, repositoryId);
        }

        // 获取对应Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);
        List<Tag> tags = rdmTagRepository.getTagsFromGitlab(glProjectId);
        return ConvertUtils.convertList(tags, TagDTO.class);
    }

    @Override
    public List<ProtectedTagDTO> getProtectedTags(Long projectId, Long repositoryId) {
        // 获取对应Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);
        List<ProtectedTag> protectedTags = rdmTagRepository.getProtectedTagsFromGitlab(glProjectId);

        return ConvertUtils.convertList(protectedTags, ProtectedTagDTO.class)
                .stream()
                .sorted(Comparator.comparing(ProtectedTagDTO::getName))
                .collect(Collectors.toList());
    }

    @Override
    public ProtectedTagDTO protectTag(Long projectId,
                                      Long repositoryId,
                                      String tagName,
                                      Integer createAccessLevel) {
        // 获取对应Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);

        // 校验标记是否已被保护
        ProtectedTag glProtectedTag = gitlabTagsApi.getProtectedTag(glProjectId, tagName);
        AssertExtensionUtils.isNull(glProtectedTag, "error.protected.tag.exist");

        ProtectedTag protectedTag = rdmTagRepository.protectTag(glProjectId, tagName, createAccessLevel);
        return ConvertUtils.convertObject(protectedTag, ProtectedTagDTO.class);
    }

    @Override
    public ProtectedTagDTO updateProtectedTag(Long projectId,
                                              Long repositoryId,
                                              String tagName,
                                              Integer createAccessLevel) {
        // 获取对应Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);

        // 由于Gitlab不提供修改保护标签的api, 故只能先删除, 再新增
        rdmTagRepository.unprotectTag(glProjectId, tagName);
        ProtectedTag protectedTag = rdmTagRepository.protectTag(glProjectId, tagName, createAccessLevel);
        return ConvertUtils.convertObject(protectedTag, ProtectedTagDTO.class);
    }

    @Override
    public void unprotectTag(Long projectId, Long repositoryId, String tagName) {
        // 获取对应Gitlab项目id
        Integer glProjectId = ic7NDevOpsServiceFacade.repositoryIdToGlProjectId(repositoryId);
        rdmTagRepository.unprotectTag(glProjectId, tagName);
    }
}
