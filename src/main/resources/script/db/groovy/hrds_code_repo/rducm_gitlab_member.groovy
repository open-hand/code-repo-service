package script.db.groovy.hrds_code_repo

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_member.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-06-05-rducm_gitlab_member") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_member_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_member", remarks: "成员表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "主键") { constraints(primaryKey: true) }
            column(name: "organization_id", type: "bigint(20)", remarks: "组织id") { constraints(nullable: "false") }
            column(name: "project_id", type: "bigint(20)", remarks: "项目层，项目id") { constraints(nullable: "false") }
            column(name: "repository_id", type: "bigint(20)", remarks: "代码仓库id") { constraints(nullable: "false") }
            column(name: "user_id", type: "bigint(20)", remarks: "用户id") { constraints(nullable: "false") }
            column(name: "gl_project_id", type: "int(11)", remarks: "gitlab项目id") { constraints(nullable: "false") }
            column(name: "gl_user_id", type: "int(11)", remarks: "gitlab用户id") { constraints(nullable: "false") }
            column(name: "gl_access_level", type: "int(11)", remarks: "gitlab成员权限级别")
            column(name: "gl_expires_at", type: "datetime", remarks: "gitlab成员过期时间")
            column(name: "sync_gitlab_flag", type: "tinyint(1)", defaultValue: "0", remarks: "gitlab同步标识") { constraints(nullable: "false") }
            column(name: "sync_gitlab_date", type: "datetime", remarks: "gitlab同步时间")
            column(name: "sync_gitlab_error_msg", type: "varchar(" + 2000 * weight + ")", remarks: "同步gitlab失败的错误信息")
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

        addUniqueConstraint(columnNames: "gl_project_id,gl_user_id", tableName: "rducm_gitlab_member", constraintName: "uk_gl_project_id_gl_user_id")
        addUniqueConstraint(columnNames: "project_id,repository_id,user_id", tableName: "rducm_gitlab_member", constraintName: "uk_project_id_repository_id_user_id")
        addUniqueConstraint(columnNames: "repository_id,user_id", tableName: "rducm_gitlab_member", constraintName: "uk_repository_id_user_id")
    }
}