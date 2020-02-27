package script.db

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_user.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-02-27-rducm_gitlab_user") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_user_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_user", remarks: "Gitlab用户表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "主键") { constraints(primaryKey: true) }
            column(name: "user_id", type: "bigint(20)", remarks: "用户id") { constraints(nullable: "false") }
            column(name: "init_password", type: "varchar(" + 40 * weight + ")", remarks: "默认密码")
            column(name: "reset_password_flag", type: "tinyint(1)", defaultValue: "0", remarks: "是否已重置密码") { constraints(nullable: "false") }
            column(name: "gl_user_id", type: "int(11)", remarks: "Gitlab用户id")
            column(name: "gl_user_name", type: "varchar(" + 40 * weight + ")", remarks: "Gitlab用户名")
            column(name: "gl_is_admin", type: "tinyint(1)", remarks: "是否是gitlab的admin")
            column(name: "gl_impersonation_token", type: "varchar(" + 64 * weight + ")", remarks: "impersonationToken")
            column(name: "sync_gitlab_flag", type: "tinyint(1)", defaultValue: "0", remarks: "gitlab同步标识") { constraints(nullable: "false") }
            column(name: "sync_date_gitlab", type: "datetime", remarks: "gitlab同步时间")
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

        addUniqueConstraint(columnNames: "gl_user_id", tableName: "rducm_gitlab_user", constraintName: "uk_gl_user_id")
        addUniqueConstraint(columnNames: "gl_user_name", tableName: "rducm_gitlab_user", constraintName: "uk_gl_user_name")
        addUniqueConstraint(columnNames: "user_id", tableName: "rducm_gitlab_user", constraintName: "uk_user_id")
    }
}