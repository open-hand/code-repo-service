package script.db

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_member_audit_log.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-04-15-rducm_gitlab_member_audit_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_member_audit_log_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_member_audit_log", remarks: "成员权限审计日志表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "主键") { constraints(primaryKey: true) }
            column(name: "organization_id", type: "bigint(20)", remarks: "组织id")
            column(name: "project_id", type: "bigint(20)", remarks: "项目id")
            column(name: "repository_id", type: "bigint(20)", remarks: "代码库id")
            column(name: "audit_no", type: "varchar(" + 128 * weight + ")", remarks: "审计流水号") { constraints(nullable: "false") }
            column(name: "audit_count", type: "int(11)", remarks: "审计记录总数") { constraints(nullable: "false") }
            column(name: "audit_start_date", type: "datetime", remarks: "审计开始日期") { constraints(nullable: "false") }
            column(name: "audit_end_date", type: "datetime", remarks: "审计结束日期") { constraints(nullable: "false") }
            column(name: "audit_duration", type: "int(11)", remarks: "审计耗时(毫秒)") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

    }
}