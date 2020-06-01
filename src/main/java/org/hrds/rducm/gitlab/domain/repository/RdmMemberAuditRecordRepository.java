package org.hrds.rducm.gitlab.domain.repository;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 成员权限审计记录表资源库
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
public interface RdmMemberAuditRecordRepository extends BaseRepository<RdmMemberAuditRecord> {
    /**
     * 将同步标识设置为true
     *
     * @param record
     * @return
     */
    int updateSyncTrueByPrimaryKeySelective(RdmMemberAuditRecord record);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsertCustom(List<RdmMemberAuditRecord> list);
}
