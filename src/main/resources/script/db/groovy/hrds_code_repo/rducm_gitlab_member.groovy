package script.db.groovy.hrds_code_repo

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_member.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-06-19-rducm_gitlab_member") {
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
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
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

        addUniqueConstraint(columnNames: "organization_id,project_id,repository_id,user_id", tableName: "rducm_gitlab_member", constraintName: "uk_project_id_repository_id_user_id")
        addUniqueConstraint(columnNames: "repository_id,user_id", tableName: "rducm_gitlab_member", constraintName: "uk_repository_id_user_id")
    }

    changeSet(author: 'wx', id: '2021-08-18-add-column') {
        addColumn(tableName: 'rducm_gitlab_member') {
            column(name: 'type', type: 'VARCHAR(20)', defaultValue: "project", afterColumn: 'project_id', remarks: '权限属于项目层还是全局层')
            column(name: 'g_group_id', type: "int(11)",  afterColumn: 'gl_project_id', remarks: 'gitlab group的id')
        }
    }
    changeSet(author: 'wx', id: '2021-08-20-alter-table'){
        sql("""
              alter table rducm_gitlab_member modify repository_id BIGINT(20) null;
              alter table rducm_gitlab_member modify gl_project_id int(11) null;
         """)
    }

}