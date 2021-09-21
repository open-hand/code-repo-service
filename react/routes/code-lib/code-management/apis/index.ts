import { axios } from '@choerodon/boot';

class CodeManagerApis {
  static axiosPostBatchPass(orgId:string, proId:string, expiresAt:string, data:any) {
    return axios.post(`/rducm/v1/organizations/${orgId}/projects/${proId}/gitlab/repositories/member-applicants/batch/pass?expiresAt=${expiresAt}`, data);
  }

  static axiosPostBatchReject(orgId:string, proId:string, approvalMessage:string, data:any) {
    return axios.post(`/rducm/v1/organizations/${orgId}/projects/${proId}/gitlab/repositories/member-applicants/batch/refuse?approvalMessage=${approvalMessage}`, data);
  }
  // 同步用户
  static asyncUser(organizationId:string, projectId:string, repositoryId:string, memberId:string,isGroup=false) {
    if(isGroup) {
      return axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/group/${memberId}/sync`);
    }
    return axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/${repositoryId}/members/${memberId}/sync`);
  }

  static getPsSetListsUrl(organizationId:string, projectId:string, repositoryIds:string){
    let tempUrl = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members`
    if(!repositoryIds || repositoryIds === 'all'){
      return tempUrl
    }
    return `${tempUrl}?repositoryIds=${repositoryIds}`
  }

  static getPsApplyUrl(organizationId:string, projectId:string, repositoryIds:string){
    let tempUrl = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants/self`
    if(!repositoryIds || repositoryIds === 'all'){
      return tempUrl
    }
    return `${tempUrl}?repositoryIds=${repositoryIds}`
  }

  static getPsApprovalUrl(organizationId:string, projectId:string, repositoryIds:string){
    let tempUrl = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants`
    if(!repositoryIds || repositoryIds === 'all'){
      return tempUrl
    }
    return `${tempUrl}?repositoryIds=${repositoryIds}`
  }

  static getAuditUrl(organizationId:string, projectId:string, repositoryIds:string){
    let tempUrl = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/member-audit-records`
    if(!repositoryIds || repositoryIds === 'all'){
      return tempUrl
    }
    return `${tempUrl}?repositoryIds=${repositoryIds}`
  }

  // 查询权限审计执行日期
  static  fetchExecutionDate(organizationId:string, projectId:string) {
    return axios.get(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/member-audit-logs/detail/latest`);
  }
      // 同步权限
  static asyncPermission(params:any) {
    let string = ''
    if(params.repositoryId) {
      string = `?repositoryId=${params.repositoryId}`
    }
    return axios.post(`/rducm/v1/organizations/${params.organizationId}/projects/${params.projectId}/member-audit-records/${params.id}/audit-fix${string}`);
  };

  static bacthfix(organizationId:string,projectId:string, data: any[]){
    return axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/member-audit-records/batch/audit-fix`, JSON.stringify(data))
  }

  static bacthAuidt(organizationId:string,projectId:string) {
    return axios.get(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/member-audit-records/audit`)
  }

  static batchAuditStatus(organizationId:string,projectId:string){
    return axios.get(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/member-audit-records/audit/status`)
  }

  static bacthfixStatus(organizationId:string,projectId:string){
    return axios.get(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/member-audit-records/audit/fix/status`)
  }
  
}

export default CodeManagerApis;