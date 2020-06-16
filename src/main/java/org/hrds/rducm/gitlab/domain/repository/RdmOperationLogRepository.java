package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.RdmOperationLog;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 操作日志表资源库
 *
 * @author ying.xie@hand-china.com 2020-02-28 10:33:02
 */
public interface RdmOperationLogRepository extends BaseRepository<RdmOperationLog> {
    /**
     * 删除指定代码库的记录
     *
     * @param organizationId
     * @param projectId
     * @param repositoryId
     * @return
     */
    int deleteByRepositoryId(Long organizationId, Long projectId, Long repositoryId);
}
