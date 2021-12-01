// zh_CN.js
const commonField = {
  // 表格通用列名
  status: '状态',
  name: '名称',
  creator: '创建者',
  updater: '更新者',
  updateDate: '更新时间',
  createDate: '创建时间',
  description: '描述',
  number: '编号',
  projectName: '项目名',
  libName: '仓库名',

  // 操作
  active: '启用',
  disable: '禁用',
  stop: '停用',
  return: '返回',
  submit: '确认',
  create: '创建',
  add: '添加',
  edit: '修改',
  save: '保存',
  refresh: '刷新',
  associate: '关联',
  loadMore: '展开更多',
  permissions: '权限分配',
  deploy: '应用部署',
  deployment: '部署',
  upgrade: '升级',
  modify: '变更',
  iknow: '我知道了',
  reset: '重置',
  install: '安装',
  uninstall: '卸载',

  // 通用描述
  app: '应用',
  appName: '应用名称',
  environment: '环境',
  envName: '环境名称',
  instance: '实例',
  network: '网络',
  label: '标签',
  version: '版本',
  file: '文件',
  commit: '提交',
  available: '可用',
  unavailable: '不可用',
  log: '日志',
  ingress: '域名',
  address: '地址',
  path: '路径',
  captcha: '验证码',
  loginName: '登录名',
  userName: '用户名',
  mailbox: '邮箱',
  password: '密码',
  projectRole: '项目角色',
  addTime: '添加时间',
  annotation: '注解',
  routing: '路由',
  port: '端口',
  protocol: '协议',
  import: '导入',
  executor: '执行者',
  startUp: '启动',
  owner: '项目所有者',
  member: '项目成员',
  all: '全部',
  not_installed: '未安装',

  // 状态描述
  null: '无',
  running: '运行中',
  operating: '处理中',
  connect: '已连接',
  disconnect: '未连接',
  stopping: '停止中',
  deleting: '删除中',
  pending: '部署中',
  stopped: '已停止',
  failed: '失败',
  deleted: '已删除',
  creating: '创建中',
  pendingcheck: '待审核',
  executing: '执行中',
  terminated: '已终止',

  // 表单通用描述
  noRepeat: '不可重复',

  // public
  detail: '详情',
  operate: '操作',
  cancel: '取消',
  close: '关闭',
  delete: '删除',
  MicroApp: '微应用',
  MicroAppUI: 'Web前端',
  Mobile: '移动',
  Application: '普通应用',
  JavaLib: 'Java 库',
  install_failed: '创建失败',
  upgrade_failed: '更新失败',
  starting_failed: '重启失败',
  stop_failed: '停止失败',
  delete_failed: '删除失败',
  deploy_failed: '部署失败',
  rollback_failed: '回滚失败',
  learnmore: '了解详情',
  required: '该字段是必输的',
  project: '本项目',
  market: '应用市场',
  share: '共享应用',
  organization: '本组织',
  public: '全平台',
  filter: '过滤表',
  previous: '上一步',
  next: '下一步',
  finish: '结束',
  ok: '确定',
  language: 'zh_CN',
  chooseType: '选择类型',
  chooseApp: '选择应用服务',
  notes: '填写内容',
  write: '编辑',
  preview: '预览',
  expand: '展开',
  shrink: '收起',
  validDate: '有效期',
  noContent: '没有内容',
  notActive: '未生效',
  expired: '已过期',
  timeFrom: '自',
  timeUntil: '至',
  success: '成功',
  minutes: '分',
  seconds: '秒',
  minTime: '时长（分）',
  nodata: '暂无数据',
  skipped: '跳过',
  more: '更多',
  total: '总次数',
  recent: '最近',
  key: '键',
  value: '值',
  detailMore: '更多详情',
  expandAll: '全部展开',
  collapseAll: '全部折叠',
  checkCodeExist: '编码已存在',
  checkCodeReg:
    '编码只能由小写字母、数字、"-"组成，且以小写字母开头，不能以"-"结尾',
  checkCodeFailed: '编码校验失败，请稍后再试',
  checkNameExist: '名称已存在',
  checkNameFailed: '名称重名校验失败，请稍后再试',
  nameCanNotHasSpaces: '名称不能包含空格',
  checkNameFail: '网络错误，请稍后重试',
  formatError: '格式有误',
  checkEmailError: '请输入有效的邮箱地址',
  nameCanNotBeEmpty: '名称不能为空',
  contentCanNotBeEmpty: '内容不能为空',

  'modify.success': '修改成功',
  'recover.success': '权限回收成功',

  // 时间
  year: '年',
  month: '月',
  week: '周',
  day: '天',
  hour: '小时',
  minute: '分钟',
  justnow: '刚刚',
  ago: '之前',

  approve: '审批',
  applicant: '申请人',
  approvalUserName: '审批人',
  yes: '是',
  no: '否',
  none: '无',
  async: '同步',
  online: '在线',
  offline: '离线',
  unit: '单位',
  startDate: '开始时间',
  endDate: '结束时间',
  projectCode: '项目编码',
  fix: '修复',
};

const userInfo = {
  // 修改密码
  'user.changepwd.twopwd.pattern.msg': '两次密码输入不一致',
  'user.changepwd.header.title': '修改密码',
  'infra.changepwd.message.svn.noContent.title': '暂无SVN账号',
  'infra.changepwd.message.svn.noContent.desc': '项目所有者给用户分配文档库权限之后，会自动创建SVN账号，路径：“项目-->文档库管理-->权限分配TAB”',
  'infra.changepwd.message.prod.noContent.title': '暂无制品库账号',
  'infra.changepwd.message.prod.noContent.desc': '仓库管理员给用户分配制品库权限之后，会自动创建制品库账号，路径：“项目-->制品库管理-->详情-->权限分配TAB”',
  'user.changepwd.gitlab': '修改仓库密码',
  'user.changepwd.oldpassword': '原密码',
  'user.changepwd.oldpassword.require.msg': '请输入原密码',
  'user.changepwd.newpassword': '新密码',
  'user.changepwd.newpassword.require.msg': '请输入新密码',
  'user.changepwd.confirmpassword.require.msg': '请确认密码',
  'user.changepwd.confirmpassword': '确认密码',
  'infra.personal.model.glWebUrl': 'GitLab个人主页',
  'infra.personal.model.createdAt': '创建时间',
  'infra.personal.model.initPassword': '初始密码',
  'infra.personal.message.modifyGitlabPassword': '修改仓库密码',
  'infra.personal.message.modifyConfirm': '确定要修改您的gitlab仓库密码吗？点击确定后，您将跳转至GitLab仓库克隆密码的修改页面。',
  'infra.personal.message.noUrl': '您所在平台暂未配置GitLab重置页面的链接，请联系平台管理员进行配置',
  'infra.personal.message.iKnow': '我知道了',
  'infra.personal.operate.updatePassword': '修改GitLab密码',
  'infra.personal.operate.resetPassword': '重置GitLab密码',
  'infra.personal.view.resetInfo': '您的GitLab密码已被重置为',
  'infra.personal.view.resetTips': '为了您的账号安全，请复制以上密码，并尽快前往GitLab修改重置后的密码。',
  'infra.personal.operate.updateInfo': '确定要重置您当前的gitlab仓库密码吗？密码重置后，为了您的账号安全，请务必尽快修改重置后的密码。',
  'infra.personal.view.toUpdate': '前往修改密码',
  'infra.personal.validate.password': '密码不能包含空格',
  'infra.personal.message.svnSetting': 'SVN设置',
  'infra.personal.message.gitLabSetting': '代码库设置',
  'infra.personal.message.prodSetting': '制品库设置',
};
const psManager = {
  infra: '权限管理',
  'infra.gitlabRole': 'gitlab角色',
  'infra.creationBy': '创建人',
  'infra.service': '应用服务',
  'infra.service.name': '服务名称',
  'infra.service.code': '服务编码',
  'infra.service.type': '服务类型',
  'infra.type.normal': '普通服务',
  'infra.type.test': '测试服务',
  'infra.icon': '服务图标',
  'infra.create': '创建应用服务',
  'infra.add': '添加人员',
  'infra.button.import-user': '导入成员',
  'infra.add.branch': '添加保护分支',
  'infra.add.tag': '添加保护标记',
  'infra.update.branch': '修改保护分支',
  'infra.update.tag': '修改保护标记',
  'infra.detail': '服务详情',
  'infra.disable': '停用服务',
  'infra.enable': '启用服务',
  'infra.version': '服务版本',
  'infra.psSet': '权限分配',
  'infra.psBranch': '分支/标记',
  'infra.psOverView': '总览',
  'infra.psSet.tips':
    '当选择将权限分配至项目下所有成员时，此时便不能在列表中删除任何成员的权限；仅在选择将权限分配给项目下特定成员时，才能在下方列表中管理成员的权限。项目所有者的权限不能被删除',
  'infra.add.tips':
    '此操作用于为此应用服务配置特定的开发操作人员。一般默认选择为项目下所有成员，即该项目下的所有成员均能对此应用服务进行开发操作；若选择项目下特定成员，则只有被添加后的成员才有权限。项目所有者默认拥有权限',
  'infra.share': '共享设置',
  'infra.project.member': '项目成员',
  'infra.project.member.empty': '暂无可选项目成员',
  'infra.project.member.require': '请选择项目成员',
  'infra.member.all': '项目下所有人员',
  'infra.member.specific': '项目下特定人员',
  'infra.add.member': '分配成员权限',
  'infra.button.batch.delete': '批量删除',
  'infra.button.batch.sync': '全部同步',
  'infra.view.message.deleteSuccess': '删除成功！',
  'infra.add.outsideMember': '分配外部成员权限',
  'infra.add.outsideMember.tips': '此处需精确输入用户名或登录名来搜索对应的用户',
  'infra.permission.request': '权限申请',
  'infra.type': '类型',
  'infra.user.all': '项目下所有人员',
  'infra.user': '成员',
  'infra.dirName': '新目录名称',
  'infra.newDirPath': '新目录路径',
  'infra.permission': '权限',
  'infra.glAccessLevel': 'Gitlab权限',
  'infra.glExpiresAt': 'Gitlab过期日期',
  'infra.syncStrategy': '同步策略',
  'infra.expiresAt': '过期日期',
  'infra.creationDate': '创建时间',
  'infra.userNumber': '用户编码',
  'infra.filter': '请输入搜索条件',
  'infra.permission.delete.title': '删除权限',
  'infra.permission.project.delete.des': '确定要删除项目的权限吗？',
  'infra.permission.delete.des': '确定要删除该成员的权限吗？',
  'infra.permission.resourcePath': '资源路径',
  'infra.permission.name': '用户名称',
  'infra.permission.recover.title': '回收权限',
  'infra.permission.project.recover.des': '确定要回收该成员的权限吗？',
  'infra.permission.assign.title': '分配权限',
  'infra.authorizedCount': '已授权服务数',
  'infra.allCount': '总服务数',
  'infra.lastUpdateDate': '更新日期',
  'infra.authorizedRate': '已授权服务占比',
  'infra.authorizedRateTips': '已授权服务的数量占所有服务的百分比。超过50%为橙色，低于50%为绿色',

  // 权限申请、审批
  'infra.applyDate': '申请时间',
  'infra.applyType': '申请类型',
  'infra.applyPermission': '申请权限',
  'infra.approvalDate': '审批时间',
  'infra.approvalMessage': '不通过原因',
  'infra.approval.pending': '待审批',
  'infra.approval.approved': '通过',
  'infra.approval.rejected': '不通过',
  'infra.approval.fields.applicantUserName': '申请人',
  'infra.approval.fields.repositoryName': '应用服务',
  'infra.approval.fields.applicantType': '申请类型',
  'infra.approval.fields.accessLevel': '申请权限',
  'infra.approval.fields.approvalState': '审批结果',
  'infra.approval.fields.approvalUserName': '审批人',
  'infra.approval.fields.approvalDate': '审批时间',
  'infra.approval.fields.approvalMessage': '不通过原因',
};

const infraCommon = {
  yes: '是',
  no: '否',
  date: '日期',
  time: '时间',
  exportAuth: '导出权限',
  createdByName: '创建人',
  creationDate: '创建时间',
  applyInfo: '申请信息',
  approvalInfo: ' 审批信息',
  reason: '原因',
  'approval.operation': '审批操作',
  'approval.message': '审批意见',
  'approval.result': '审批结果',
  'confirm.delete': '确认删除？',
  'confirm.cancleAuthorization': '确认取消授权？',
  'success.delete': '删除成功',
  'success.copy': '复制成功',
  'success.cancleAuthorization': '取消授权成功',
  'success.export': '导出成功',
  'exportModal.confirm.title': '权限导出确认',
  'success.operation': '操作成功',
  'view.log': '构建日志',
  'commit.success': '提交成功',
  'save.success': '保存成功',
  'ldap.disable': '禁用LDAP',
  'ldap.enable': '启用LDAP',
  'view.detail': '查看详情',
};

const codeManagement = {
  'infra.docManage.message.exportSuccess': '导出成功',
  'infra.docManage.message.exportConfirm': '权限导出确认',
  'infra.docManage.message.confirm.export': '确定导出',
  'dir.path.permission': '的{dirData}权限?',
  'infra.codeManage.service.name': '服务名称',
  'infra.codeManage.creationDate': '创建日期',
  'infra.codeManage.branch': '分支',
  'infra.codeManage.tag': '标记',
  'infra.codeManage.branch.protected.set': '保护分支设置',
  'infra.codeManage.tag.protected.set': '保护标记设置',
  'infra.codeManage.isAllow.merge': '是否允许合并',
  'infra.codeManage.isAllow.push': '是否允许推送',
  'infra.codeManage.isAllow.create': '是否允许创建或更新',
  'infra.codeManage.branch.protected': '受保护的分支',
  'infra.codeManage.tag.protected': '受保护的标记',
  'infra.codeManage.allow.merge': '允许合并',
  'infra.codeManage.allow.push': '允许推送',
  'infra.codeManage.allow.create': '允许创建',
  'infra.codeManage.cancel.protected': '取消保护',
  'infra.codeManage.cancel.protected.confirm': '确定取消保护该标记？',
  'infra.codeManage.cancel.protected.confirm.branch': '确定取消保护该分支？',
  'infra.codeManage.cancel.protected.confirm.tag': '确定取消保护该标记？',
  'infra.codeManage.develop.managerCountTips': '权限大于或等于Maintainer的成员数量',
  'infra.codeManage.develop.managerCount': '管理成员总数',
  'infra.codeManage.develop.allMember': '开发成员总数',
  'infra.codeManage.default.branch': '默认分支',
  'infra.codeManage.lib.visible': '仓库可见性',
  'infra.codeManage.last.commit': '最近一次提交',
  'infra.codeManage.wait.handle.number': '待处理合并请求数',
  'infra.codeManage.ps.message.noApprove': '请审批后再提交',
  'infra.codeManage.ps.message.noReason': '请输入原因',
  'infra.codeManage.ps.approveSuccess': '审批成功',
  'infra.codeManage.ps.model.approveInfo': '审批情况',
  'infra.codeManage.ps.model.approveResult': '审批结果',
  'infra.codeManage.ps.model.approved': '通过',
  'infra.codeManage.ps.model.rejected': '不通过',
  'infra.codeManage.ps.model.approveTime': '审批时间',
  'infra.codeManage.ps.model.approveUser': '审批人',
  'infra.codeManage.ps.model.rejectedReason': '不通过原因',
  'infra.codeManage.ps.model.applyInfo': '申请信息',
  'infra.codeManage.ps.model.expDate': '过期日期',
  'infra.codeManage.ps.model.select': '审批信息',
  'infra.codeManage.ps.message.wetherToPass': '是否通过审批',
  'infra.codeManage.ps.message.enterReason': '请输入原因',
  'infra.codeManage.ps.message.asyncSuccess': '同步成功！',
  'infra.codeManage.ps.model.syncStrategy': '同步策略',
  'infra.codeManage.ps.model.currentToGitlab': '当前 -> Gitlab',
  'infra.codeManage.ps.model.gitlabToCurrent': '当前 <- Gitlab',
  'infra.operate.export.permission': '导出权限',
  'infra.codeManage.ps.message.apply.detail': '审批详情',
  'infra.codeManage.ps.message.approveDrawerTitle': '审批“{name}”的权限申请',
  'infra.codeManage.ps.message.approveDetail': '“{name}”的审批详情',
  'infra.codeManage.ps.operate.fixPs': '修复权限',
  'infra.codeManage.ps.operate.fixPs.confirm': '确认将Gitlab权限修复为与代码库成员一致?',
  'infra.codeManage.ps.model.executionDate': '审计执行日期：',
  'infra.codeManage.ps.model.diffCount': '差异数据总数：',
  'infra.codeManage.ps.message.modifyPs': '修改成员权限',
  'infra.codeManage.ps.message.asyncTips': '同步Gitlab成员至本系统，若Gitlab成员不存在，该成员将被移除',
  'infra.codeManage.ps.message.psDetail': '用户“{name}”的应用服务权限详情',
  'infra.codeManage.ps.message.psApproval': '权限审批',
  'infra.codeManage.ps.message.applyView': '权限申请',
  'infra.codeManage.ps.message.psAudit': '权限审计',
  'infra.codeManage.ps.message.psAudit.tips': '系统每月1号会自动触发【权限审计】操作，去校验C7N中项目成员的代码权限与在Gitlab中权限的差异。也可点击【手动审计】人为触发【权限审计】。对于差异数据可通过【修复】将C7N中设置的代码权限修复为与Gitlab中一致。',
  'infra.codeManage.ps.message.securityAudit': '安全审计',
  'infra.codeManage.ps.message.psBranch': '保护分支/标记',
  'infra.codeManage.ps.message.operationLog': '操作日志',
  'infra.codeManage.ps.message.approveSuccess': '审批成功',

  'infra.codeManage.repositoryName': '服务名',

  'infra.codeManage.operationLog.model.startDate': '开始日期',
  'infra.codeManage.operationLog.model.endDate': '结束日期',
  'infra.codeManage.operationLog.model.repositoryId': '应用服务',
  'infra.codeManage.operationLog.model.opUserName': '操作人',
  'infra.codeManage.operationLog.model.opType': '操作类型',
  'infra.codeManage.operationLog.model.opContent': '操作内容',
  'infra.codeManage.operationLog.model.opDate': '操作日期',
  'infra.codelib.audit.model.startDate': '开始日期',
  'infra.codelib.audit.model.endDate': '结束日期',
  'infra.codelib.audit.model.projectId': '项目',
  'infra.codelib.audit.model.opUserName': '操作人',
  'infra.codelib.audit.model.opType': '操作类型',
  'infra.codelib.audit.model.opContent': '操作内容',
  'infra.codelib.audit.model.opDate': '操作日期',
  'infra.codelib.audit.model.service': '应用服务',
  'infra.codelib.audit.model.permission': '权限',
  'infra.codelib.audit.model.glExpiresAt': '过期日期',
  'infra.codelib.audit.model.creationBy': '创建人',
  'infra.codelib.audit.view.psView': '权限查看',
  'infra.codelib.audit.view.allProject': '所有项目',
  'infra.codelib.audit.view.puAudit': '权限审计',
  'infra.codelib.audit.view.optLog': '操作日志',
  'infra.codelib.audit.view.loadMore': '加载更多',
  'infra.codelib.audit.view.noLog': '暂无操作记录',
  'infra.codelib.audit.model.projectName': '项目名称',
};

const zhCN = {
  ...commonField,
  ...userInfo,
  ...psManager,
  ...codeManagement,
  ...infraCommon,
};

export { zhCN };

export * from './personalSetting';

export * from './code-lib-org';
