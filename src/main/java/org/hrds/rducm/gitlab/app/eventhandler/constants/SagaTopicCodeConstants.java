package org.hrds.rducm.gitlab.app.eventhandler.constants;

/**
 * 此类放sagaTopicCode常量
 */
public class SagaTopicCodeConstants {
    /**
     * 代码仓库添加成员
     */
    public static final String RDUCM_BATCH_ADD_MEMBERS = "rducm-batch-add-members";

    /* 项目成员角色增删改同步代码库 */
    /**
     * IAM更新角色
     */
    public static final String IAM_UPDATE_MEMBER_ROLE = "iam-update-memberRole";

    /**
     * IAM删除角色
     */
    public static final String IAM_DELETE_MEMBER_ROLE = "iam-delete-memberRole";

    /* Devops创建应用服务 */

    /**
     * Devops创建应用服务
     */
    public static final String DEVOPS_CREATE_APPLICATION_SERVICE = "devops-create-application-service";

    /**
     * Devops从外部代码平台导入到gitlab项目
     */
    public static final String DEVOPS_IMPORT_GITLAB_PROJECT = "devops-import-gitlab-project";

    /**
     * Devopsn导入应用服务（内部）
     */
    public static final String DEVOPS_IMPORT_INTERNAL_APPLICATION_SERVICE = "devops-import-internal-application-service";


    /* Devops删除应用服务 */

    /**
     * Devops删除应用服务
     */
    public static final String DEVOPS_APP_DELETE = "devops-app-delete";

    private SagaTopicCodeConstants() {
    }
}
