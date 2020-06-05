package org.hrds.rducm.gitlab.infra.repository.impl;

import org.hrds.rducm.gitlab.domain.entity.MemberAuditLog;
import org.hrds.rducm.gitlab.domain.repository.MemberAuditLogRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * 成员权限审计日志表 资源库实现
 *
 * @author ying.xie@hand-china.com 2020-04-02 14:57:44
 */
@Component
public class MemberAuditLogRepositoryImpl extends BaseRepositoryImpl<MemberAuditLog> implements MemberAuditLogRepository {


}
