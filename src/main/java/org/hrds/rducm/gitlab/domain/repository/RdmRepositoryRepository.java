package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.RdmRepository;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 资源库
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
public interface RdmRepositoryRepository extends BaseRepository<RdmRepository> {
    /**
     * 根据唯一索引查询
     *
     * @param repositoryId
     * @return
     */
    RdmRepository selectByUk(Long repositoryId);
}
