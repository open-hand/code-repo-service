package org.hrds.rducm.gitlab.app.eventhandler.gitlab;

import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

/**
 * Created by wangxiang on 2021/8/26
 */
public interface GitlabPermissionHandler {
    void gitlabPermissionRepair(RdmMemberAuditRecord rdmMemberAuditRecord);
}
