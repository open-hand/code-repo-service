export default class ApproveApis {
  static getBatchPassUrl(orgId, proId, expiresAt) {
    let baseUrl = `/rducm/v1/organizations/${orgId}/projects/${proId}/gitlab/repositories/member-applicants/batch/pass`;
    if (expiresAt) {
      baseUrl += `?expiresAt=${expiresAt}`;
    }
    return baseUrl;
  }

  static getBatchRejectUrl(orgId, proId, approvalMessage) {
    return `/rducm/v1/organizations/${orgId}/projects/${proId}/gitlab/repositories/member-applicants/batch/refuse?approvalMessage=${approvalMessage}`;
  }
}
