package script.db.groovy.hrds_code_repo

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_member_audit_record.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-06-19-rducm_gitlab_member_audit_record") {
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
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
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


    changeSet(author: 'wx', id: '2021-08-24-add-column') {
        addColumn(tableName: 'rducm_gitlab_member_audit_record') {
            column(name: 'type', type: 'VARCHAR(20)', defaultValue: "project", afterColumn: 'project_id', remarks: '权限属于项目层还是全局层')
            column(name: 'g_group_id', type: "int(11)",  afterColumn: 'gl_project_id', remarks: 'gitlab group的id')
        }
        sql("""
              alter table rducm_gitlab_member_audit_record modify repository_id BIGINT(20) null;
              alter table rducm_gitlab_member_audit_record modify gl_project_id int(11) null;
         """)
    }

}