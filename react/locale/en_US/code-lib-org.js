// ~~代码库管理
const codeLibOrgTabPermission = {
  'c7ncd.code-lib-org.permission.allProject': 'All Project',
  'c7ncd.code-lib-org.permission.permission': 'Permission',
  'c7ncd.code-lib-org.permission.permissionToExport': 'Export',
  'c7ncd.code-lib-org.permission.projectName': 'Project Name',
  'c7ncd.code-lib-org.permission.applicationServices': 'Application Services',
  'c7ncd.code-lib-org.permission.projectRoles': 'Project Roles',
  'c7ncd.code-lib-org.permission.permissions': 'Permissions',
  'c7ncd.code-lib-org.permission.expirationDate': 'Expiration Date',
  'c7ncd.code-lib-org.permission.user': 'User',
  'c7ncd.code-lib-org.permission.creationTime': 'Creation Time',
};

const codeLibOrgTabAudit = {
  'c7ncd.code-lib-org.audit.tips': 'The system will automatically trigger the [Permission Audit] operation on the 1st of every month to verify the difference between the code permissions of project members in C7N and those in Gitlab. You can also click "Manual Audit" to manually trigger "permission audit". For differential data, you can fix the code permissions set in C7N to be consistent with those set in Gitlab through [Fix].',
  'c7ncd.code-lib-org.audit.audit': 'Audit',
  'c7ncd.code-lib-org.audit.auditExecutionDate': 'AuditExecution Date',
  'c7ncd.code-lib-org.audit.totalVarianceData': 'Total VarianceData',
  'c7ncd.code-lib-org.audit.projectName': 'Project Name',
  'c7ncd.code-lib-org.audit.applicationServices': 'Application Services',
  'c7ncd.code-lib-org.audit.permissions': 'Permissions',
  'c7ncd.code-lib-org.audit.expirationDate': 'Expiration Date',
  'c7ncd.code-lib-org.audit.gitlabPermissions': 'Gitlab Permissions',
  'c7ncd.code-lib-org.audit.gitlabOverdue': 'Gitlab Overdue',
};

const codeLibOrgTabLog = {
  'c7ncd.code-lib-org.log.project': 'Project',
  'c7ncd.code-lib-org.log.log': 'Log',
  'c7ncd.code-lib-org.log.operator': 'Operator',
  'c7ncd.code-lib-org.log.startDate': 'Start Date',
  'c7ncd.code-lib-org.log.endDate': 'End Date',
  'c7ncd.code-lib-org.log.operationType': 'Operation Type',
  'c7ncd.code-lib-org.log.noOperationRecord': 'There is no operation record',
};

export { codeLibOrgTabPermission, codeLibOrgTabAudit, codeLibOrgTabLog };

