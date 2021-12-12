// ~~代码库管理
const codeLibOrgTabPermission = {
  'c7ncd.code-lib-org.permission.allProject': '所有项目',
  'c7ncd.code-lib-org.list.search': '请输入搜索条件',
  'c7ncd.code-lib-org.permission.permission': '权限查看',
  'c7ncd.code-lib-org.permission.permissionToExport': '导出权限',
  'c7ncd.code-lib-org.permission.projectName': '项目名称',
  'c7ncd.code-lib-org.permission.applicationServices': '应用服务',
  'c7ncd.code-lib-org.permission.projectRoles': '项目角色',
  'c7ncd.code-lib-org.permission.permissions': '权限',
  'c7ncd.code-lib-org.permission.expirationDate': '过期日期',
  'c7ncd.code-lib-org.permission.user': '创建人',
  'c7ncd.code-lib-org.permission.creationTime': '添加时间',
};

const codeLibOrgTabAudit = {
  'c7ncd.code-lib-org.audit.audit': '权限审计',
  'c7ncd.code-lib-org.audit.tips': '系统每月1号会自动触发【权限审计】操作，去校验C7N中项目成员的代码权限与在Gitlab中权限的差异。也可点击【手动审计】人为触发【权限审计】。对于差异数据可通过【修复】将C7N中设置的代码权限修复为与Gitlab中一致。',
  'c7ncd.code-lib-org.audit.auditExecutionDate': '审计执行日期',
  'c7ncd.code-lib-org.audit.totalVarianceData': '差异数据总数',
  'c7ncd.code-lib-org.audit.projectName': '项目名称',
  'c7ncd.code-lib-org.audit.applicationServices': '应用服务',
  'c7ncd.code-lib-org.audit.permissions': '权限',
  'c7ncd.code-lib-org.audit.expirationDate': '过期日期',
  'c7ncd.code-lib-org.audit.gitlabPermissions': 'Gitlab权限',
  'c7ncd.code-lib-org.audit.gitlabOverdue': 'Gitlab过期',
};

const codeLibOrgTabLog = {
  'c7ncd.code-lib-org.log.project': '项目',
  'c7ncd.code-lib-org.log.log': '操作日志',
  'c7ncd.code-lib-org.log.operator': '操作人',
  'c7ncd.code-lib-org.log.startDate': '开始日期',
  'c7ncd.code-lib-org.log.endDate': '结束日期',
  'c7ncd.code-lib-org.log.operationType': '操作类型',
  'c7ncd.code-lib-org.log.noOperationRecord': '暂无操作记录',
};

export { codeLibOrgTabPermission, codeLibOrgTabAudit, codeLibOrgTabLog };

