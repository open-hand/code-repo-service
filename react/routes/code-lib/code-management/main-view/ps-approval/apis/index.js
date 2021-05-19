export default class ApproveApis {
  static getBatchPassUrl(orgId, proId, expiresAt) {
    return `/rducm/v1/organizations/${orgId}/projects/${proId}/gitlab/repositories/member-applicants/batch/pass?expiresAt=${expiresAt}`;
  }

  static getBatchRejectUrl(orgId, proId, approvalMessage) {
    return `/rducm/v1/organizations/${orgId}/projects/${proId}/gitlab/repositories/member-applicants/batch/refuse?approvalMessage=${approvalMessage}`;
  }
}
