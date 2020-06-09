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
    public static final String CODE_REPO_UPDATE_MEMBER = "codeRepoUpdateMember";

    private SagaTaskCodeConstants() {
    }
}