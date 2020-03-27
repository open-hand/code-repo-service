package script.db

databaseChangeLog(logicalFilePath: 'script/db/rducm_gitlab_repository.groovy') {
    changeSet(author: "ying.xie@hand-china.com", id: "2020-03-03-rducm_gitlab_repository") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'rducm_gitlab_repository_s', startValue: "1")
        }
        createTable(tableName: "rducm_gitlab_repository", remarks: "代码库表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "主键") { constraints(primaryKey: true) }
            column(name: "organization_id", type: "bigint(20)", remarks: "组织id") { constraints(nullable: "false") }
            column(name: "project_id", type: "bigint(20)", remarks: "项目id") { constraints(nullable: "false") }
            column(name: "repository_id", type: "bigint(20)", remarks: "代码库id") { constraints(nullable: "false") }
            column(name: "repository_name", type: "varchar(" + 40 * weight + ")", remarks: "代码库名称（即应用服务名称）")
            column(name: "gl_project_id", type: "int(11)", remarks: "Gitlab项目id（代码库）") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

        addUniqueConstraint(columnNames: "gl_project_id", tableName: "rducm_gitlab_repository", constraintName: "uk_gl_project_id")
        addUniqueConstraint(columnNames: "repository_id", tableName: "rducm_gitlab_repository", constraintName: "uk_repository_id")
    }
}