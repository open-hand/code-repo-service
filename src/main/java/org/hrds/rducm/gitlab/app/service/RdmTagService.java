package org.hrds.rducm.gitlab.app.service;

import org.hrds.rducm.gitlab.api.controller.dto.tag.ProtectedTagDTO;
import org.hrds.rducm.gitlab.api.controller.dto.tag.TagDTO;

import java.util.List;

public interface RdmTagService {

    List<TagDTO> getTags(Long repositoryId);

    List<ProtectedTagDTO> getProtectedTags(Long repositoryId);

    ProtectedTagDTO protectTag(Long repositoryId,
                               String tagName,
                               Integer createAccessLevel);

    ProtectedTagDTO updateProtectedTag(Long repositoryId,
                                       String tagName,
                                       Integer createAccessLevel);

    void unprotectTag(Long repositoryId, String tagName);
}
