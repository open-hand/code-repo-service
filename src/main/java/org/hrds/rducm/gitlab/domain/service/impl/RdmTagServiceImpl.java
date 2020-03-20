package org.hrds.rducm.gitlab.domain.service.impl;

import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmRepositoryRepository;
import org.hrds.rducm.gitlab.domain.repository.RdmTagRepository;
import org.hrds.rducm.gitlab.domain.service.IC7nDevOpsServiceService;
import org.hrds.rducm.gitlab.domain.service.IRdmTagService;
import org.hrds.rducm.gitlab.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
@Service
public class RdmTagServiceImpl implements IRdmTagService {
    @Autowired
    private RdmTagRepository rdmTagRepository;
    @Autowired
    private IC7nDevOpsServiceService ic7nDevOpsServiceService;

    @Override
    public List<TagDTO> getTagsWithExcludeProtected(Long projectId, Long repositoryId) {
        // 获取对应Gitlab项目id
        Integer glProjectId = ic7nDevOpsServiceService.repositoryIdToGlProjectId(projectId, repositoryId);

        // 获取标记
        List<Tag> tags = rdmTagRepository.getTagsFromGitlab(glProjectId);

        // 获取保护标记
        List<ProtectedTag> protectedTags = rdmTagRepository.getProtectedTagsFromGitlab(glProjectId);
        Set<String> tagNameSet = protectedTags.stream().map(pt -> pt.getName()).collect(Collectors.toSet());


        // 排除保护标记
        if (!tagNameSet.isEmpty()) {
            tags.removeIf(tag -> tagNameSet.contains(tag.getName()));
        }

        return ConvertUtils.convertList(tags, TagDTO.class);
    }
}
