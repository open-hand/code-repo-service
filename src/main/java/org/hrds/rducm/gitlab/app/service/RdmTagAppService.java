package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.tag.ProtectedTagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagQueryDTO;

import java.util.List;

public interface RdmTagAppService {
    /**
     * 查询标记列表
     *
     * @param repositoryId
     * @param tagQueryDTO 参数
     * @return
     */
    List<TagDTO> getTags(Long repositoryId, TagQueryDTO tagQueryDTO);

    /**
     * 查询保护标记列表
     *
     * @param repositoryId
     * @return
     */
    List<ProtectedTagDTO> getProtectedTags(Long repositoryId);

    /**
     * 设置保护标记
     *
     * @param repositoryId
     * @param tagName
     * @param createAccessLevel
     * @return
     */
    ProtectedTagDTO protectTag(Long repositoryId,
                               String tagName,
                               Integer createAccessLevel);

    /**
     * 更新保护标记
     *
     * @param repositoryId
     * @param tagName
     * @param createAccessLevel
     * @return
     */
    ProtectedTagDTO updateProtectedTag(Long repositoryId,
                                       String tagName,
                                       Integer createAccessLevel);

    /**
     * 取消保护标记
     *
     * @param repositoryId
     * @param tagName
     */
    void unprotectTag(Long repositoryId, String tagName);
}
