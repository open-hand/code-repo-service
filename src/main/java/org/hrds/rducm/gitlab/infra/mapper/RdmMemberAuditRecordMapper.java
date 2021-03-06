package org.hrds.rducm.gitlab.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

import java.util.List;

/**
 * 成员权限审计日志表Mapper
 *
 * @author ying.xie@hand-china.com 2020-03-30 14:09:52
 */
public interface RdmMemberAuditRecordMapper extends BaseMapper<RdmMemberAuditRecord> {
    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsertCustom(List<RdmMemberAuditRecord> list);
}
