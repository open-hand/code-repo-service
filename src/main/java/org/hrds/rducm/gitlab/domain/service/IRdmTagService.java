package org.hrds.rducm.gitlab.domain.service;

import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;

import java.util.List;

/**
 * @author ying.xie@hand-china.com
 * @date 2020/3/6
 */
public interface IRdmTagService {
    /**
     * 获取分支列表(排除保护分支)
     *
     * @param projectId
     * @param repositoryId
     * @return
     */
    List<TagDTO> getTagsWithExcludeProtected(Long projectId, Long repositoryId);
}
