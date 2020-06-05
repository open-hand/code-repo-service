package org.hrds.rducm.gitlab.domain.repository;

import org.gitlab4j.api.models.ProtectedTag;
import org.gitlab4j.api.models.Tag;

import java.util.List;

public interface RdmTagRepository {
    /**
     * 查询Gitlab标记
     *
     * @param projectId Gitlab项目id
     * @return
     */
    List<Tag> getTagsFromGitlab(Integer projectId);

    /**
     * 查询Gitlab保护标记
     *
     * @param projectIdOrPath Gitlab项目id或地址
     * @return
     */
    List<ProtectedTag> getProtectedTagsFromGitlab(Object projectIdOrPath);

    /**
     * 设置保护标记
     *
     * @param projectIdOrPath Gitlab项目id或地址
     * @param name
     * @param createAccessLevel
     * @return
     */
    ProtectedTag protectTag(Object projectIdOrPath, String name, Integer createAccessLevel);

    /**
     * 取消保护标记
     *
     * @param projectIdOrPath Gitlab项目id或地址
     * @param name
     */
    void unprotectTag(Object projectIdOrPath, String name);
}
