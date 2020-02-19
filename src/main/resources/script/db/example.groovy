package script.db

databaseChangeLog(logicalFilePath: 'script/db/example.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-04-08-example") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'example_s', startValue: "1")
        }
        createTable(tableName: "example", remarks: "") {
            column(name: "id", type: "bigint(20)", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "code", type: "varchar(" + 64 * weight + ")", remarks: "编码") { constraints(nullable: "false") }
            column(name: "name", type: "varchar(" + 64 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "")
            column(name: "created_by", type: "bigint(20)", defaultValue: "0", remarks: "")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "")
            column(name: "last_updated_by", type: "bigint(20)", defaultValue: "0", remarks: "")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "")

        }

        addUniqueConstraint(columnNames: "code", tableName: "example", constraintName: "example_u1")
    }
}