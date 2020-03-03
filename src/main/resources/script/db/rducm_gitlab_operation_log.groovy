package script.db

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_operation_log.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-03-03-rducm_gitlab_operation_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_operation_log_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_operation_log", remarks: "操作日志表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "主键") { constraints(primaryKey: true) }
            column(name: "project_id", type: "bigint(20)", remarks: "项目层，项目id") { constraints(nullable: "false") }
            column(name: "repository_id", type: "bigint(20)", remarks: "代码仓库id") { constraints(nullable: "false") }
            column(name: "op_user_id", type: "bigint(20)", remarks: "操作人，用户id") { constraints(nullable: "false") }
            column(name: "op_type", type: "varchar(" + 40 * weight + ")", remarks: "操作类型(成员管理|分支管理)") { constraints(nullable: "false") }
            column(name: "op_event_type", type: "varchar(" + 40 * weight + ")", remarks: "操作事件类型") { constraints(nullable: "false") }
            column(name: "op_content", type: "varchar(" + 1000 * weight + ")", remarks: "操作内容")
            column(name: "op_target", type: "varchar(" + 40 * weight + ")", remarks: "操作对象")
            column(name: "op_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "操作日期") { constraints(nullable: "false") }
            column(name: "extra_param", type: "text", remarks: "额外参数")
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

    }
}