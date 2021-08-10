
const psSetTabData = {
  name: "权限分配",
  value: "psSet",
  tip: "",
}

const psApprovalTabData ={
  name: "权限审批",
  value: "psApproval",
}

const psAuditTabData = {
  name: "权限审计",
  value: "psAudit",
  tip: '权限审计模块每月1号将自动去校验Choerodon中成员权限和GitLab的成员权限的差异。对于存在差异的数据，可点击修复按钮将系统内用户的应用服务权限更新为与GitLab的一致。',
}

const applyViewTabData ={
  name: "权限申请",
  value: "applyView",
}

export {
  applyViewTabData,
  psAuditTabData,
  psApprovalTabData,
  psSetTabData,
}
