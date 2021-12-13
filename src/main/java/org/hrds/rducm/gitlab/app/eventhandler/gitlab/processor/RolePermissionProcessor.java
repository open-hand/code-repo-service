package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

/**
 * Created by wangxiang on 2021/10/23
 */
public interface RolePermissionProcessor {

    /**
     * 根据角色修复项目层的权限
     *
     * @param member
     * @param dbRdmMember
     * @param rdmMemberAuditRecord
     */
    void repairProjectPermissionByRole(Member member, Member groupGlMember, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord);

}
