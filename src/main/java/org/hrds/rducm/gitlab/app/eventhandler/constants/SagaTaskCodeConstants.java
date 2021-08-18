package org.hrds.rducm.gitlab.app.eventhandler.constants;

/**
 * 此类放sagaTaskCode常量
 */
public class SagaTaskCodeConstants {
    public static final String RDUCM_BATCH_ADD_MEMBERS_TO_GITLAB = "rducmBatchAddMembersToGitlab";

    /* 项目成员角色增删改同步代码库 */
    /**
     * 更新角色同步事件
     */
    public static final String CODE_REPO_UPDATE_MEMBER_ROLE = "codeRepoUpdateMemberRole";

    /**
     * 删除角色同步事件
     */
    public static final String CODE_REPO_DELETE_MEMBER_ROLE = "codeRepoDeleteMemberRole";

    /* 应用服务创建事件 */
    /**
     * 代码库初始化权限
     */
    public static final String CODE_REPO_INIT_PRIVILEGE = "codeRepoInitPrivilege";

    /* 应用服务删除事件 */
    /**
     * 代码库删除应用服务权限
     */
    public static final String CODE_REPO_DELETE_PRIVILEGE = "codeRepoDeletePrivilege";

    /* 应用服务停用/启用事件 */
    /**
     * 代码库[生效/失效]应用服务权限
     */
    public static final String CODE_REPO_VALID_PRIVILEGE = "codeRepoValidPrivilege";

    public static final String BATCH_ADD_GITLAB_MEMBER = "batchAddOrUpdateMembers";

    public static final String PROJECT_AUDIT_MEMBER_PERMISSION = "projectAuditMemberPermission";

    /**
     * 项目下修复成员
     */
    public static final String PROJECT_BATCH_AUDIT_FIX = "projectBatchAuditFix";

    public static final String BATCH_ADD_GROUP_MEMBERS = "batchAddGroupMembers";


    private SagaTaskCodeConstants() {
    }
}