package script.db.groovy.hrds_code_repo

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_member_audit_record.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-06-05-rducm_gitlab_member_audit_record") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_member_audit_record_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_member_audit_record", remarks: "成员权限审计记录表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "主键") { constraints(primaryKey: true) }
            column(name: "organization_id", type: "bigint(20)", remarks: "组织id") { constraints(nullable: "false") }
            column(name: "project_id", type: "bigint(20)", remarks: "项目层，项目id") { constraints(nullable: "false") }
            column(name: "repository_id", type: "bigint(20)", remarks: "代码仓库id") { constraints(nullable: "false") }
            column(name: "user_id", type: "bigint(20)", remarks: "用户id")
            column(name: "access_level", type: "int(11)", remarks: "gitlab成员权限级别(本系统)")
            column(name: "expires_at", type: "datetime", remarks: "gitlab成员过期时间(本系统)")
            column(name: "gl_project_id", type: "int(11)", remarks: "gitlab项目id") { constraints(nullable: "false") }
            column(name: "gl_user_id", type: "int(11)", remarks: "gitlab用户id")
            column(name: "gl_access_level", type: "int(11)", remarks: "gitlab成员权限级别(Gitlab)")
            column(name: "gl_expires_at", type: "datetime", remarks: "gitlab成员过期时间(Gitlab)")
            column(name: "sync_flag", type: "tinyint(1)", defaultValue: "0", remarks: "同步标识") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

    }
}