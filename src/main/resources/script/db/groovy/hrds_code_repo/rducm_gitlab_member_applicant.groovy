package script.db.groovy.hrds_code_repo

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_member_applicant.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-06-19-rducm_gitlab_member_applicant") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_member_applicant_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_member_applicant", remarks: "成员权限申请表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "organization_id", type: "bigint(20)", remarks: "组织id") { constraints(nullable: "false") }
            column(name: "project_id", type: "bigint(20)", remarks: "项目id") { constraints(nullable: "false") }
            column(name: "repository_id", type: "bigint(20)", remarks: "代码库id") { constraints(nullable: "false") }
            column(name: "applicant_user_id", type: "bigint(20)", remarks: "申请人，用户id") { constraints(nullable: "false") }
            column(name: "applicant_type", type: "varchar(" + 40 * weight + ")", remarks: "申请类型（新成员|权限变更）") { constraints(nullable: "false") }
            column(name: "applicant_date", type: "datetime", remarks: "申请时间") { constraints(nullable: "false") }
            column(name: "approval_user_id", type: "bigint(20)", remarks: "审批人，用户id")
            column(name: "approval_state", type: "varchar(" + 40 * weight + ")", remarks: "审批状态（待审批|通过|不通过）") { constraints(nullable: "false") }
            column(name: "approval_date", type: "datetime", remarks: "审批时间")
            column(name: "approval_message", type: "varchar(" + 400 * weight + ")", remarks: "审批信息")
            column(name: "access_level", type: "int(11)", remarks: "权限等级") { constraints(nullable: "false") }
            column(name: "old_access_level", type: "int(11)", remarks: "旧权限等级")
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

    }
}