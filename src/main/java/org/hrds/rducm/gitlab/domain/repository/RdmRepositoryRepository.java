package org.hrds.rducm.gitlab.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hrds.rducm.gitlab.domain.entity.RdmRepository;

/**
 * 资源库
 *
 * @author ying.xie@hand-china.com 2020-02-26 14:03:22
 */
public interface RdmRepositoryRepository extends BaseRepository<RdmRepository> {

    RdmRepository selectByUk(Long repositoryId);
}
