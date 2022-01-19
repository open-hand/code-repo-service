import React from 'react';

const psSetTabData = (format: any) => ({
  name: format({ id: 'PermissionAssignment' }),
  value: "psSet",
  tip: "",
})

const psApprovalTabData = (format: any) => ({
  name: format({ id: 'PermissionApproval' }),
  value: "psApproval",
})

const psAuditTabData = (format: any) => ({
  name: format({ id: 'PermissionAudit' }),
  value: "psAudit",
  tip: '系统每月1号会自动触发【权限审计】操作，去校验C7N中项目成员的代码权限与在Gitlab中权限的差异。也可点击【手动审计】人为触发【权限审计】。对于差异数据可通过【修复】将C7N中设置的代码权限修复为与Gitlab中一致。',
})


const applyViewTabData = {
  name: "权限申请",
  value: "applyView",
}

export {
  applyViewTabData,
  psAuditTabData,
  psApprovalTabData,
  psSetTabData,
}
