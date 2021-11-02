package org.hrds.rducm.gitlab.app.eventhandler.gitlab.processor;

import org.gitlab4j.api.models.Member;
import org.hrds.rducm.gitlab.domain.entity.RdmMember;
import org.hrds.rducm.gitlab.domain.entity.RdmMemberAuditRecord;

/**
 * Created by wangxiang on 2021/10/23
 */
public interface RolePermissionProcessor {
    //根据用户的角色来进行权限的修复

    void repairGroupPermissionByRole(Member groupGlMember, RdmMember rdmMember, RdmMemberAuditRecord rdmMemberAuditRecord);


    void repairProjectPermissionByRole(Member member, RdmMember dbRdmMember, RdmMemberAuditRecord rdmMemberAuditRecord);



//    //操作gitlab权限
//    void repairGitlabPermissionByRole(Member groupGlMember, RdmMemberAuditRecord rdmMemberAuditRecord);

}
