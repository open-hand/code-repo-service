import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    oldOptsRecord: [],
    setOldOptsRecord(data) {
      this.oldOptsRecord = data || [];
    },
    get getOldOptsRecord() {
      return this.oldOptsRecord;
    },

    // 获取应用服务的仓库地址
    loadServiceUrl(projectId, appId) {
      return axios.get(`/devops/v1/projects/${projectId}/app_service/${appId}/git/url`);
    },
    // 审批通过
    approvalPass(organizationId, projectId, id, objectVersionNumber, data) {
      const params = data ? `?objectVersionNumber=${objectVersionNumber}&expiresAt=${encodeURIComponent(data)}` : `?objectVersionNumber=${objectVersionNumber}`;
      return axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants/${id}/pass${params}`);
    },
    // 审批拒绝
    approvalRefuse(organizationId, projectId, id, objectVersionNumber, data) {
      return axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants/${id}/refuse?objectVersionNumber=${objectVersionNumber}`, data);
    },
    // 同步用户
    asyncUser(organizationId, projectId, repositoryId, memberId) {
      return axios.post(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/${repositoryId}/members/${memberId}/sync`);
    },
    // 查询权限审计执行日期
    fetchExecutionDate(organizationId, projectId) {
      return axios.get(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/member-audit-logs/detail/latest`);
    },
    // 同步权限
    asyncPermission(params) {
      return axios.post(`/rducm/v1/organizations/${params.organizationId}/projects/${params.projectId}/member-audit-records/${params.id}/audit-fix?repositoryId=${params.repositoryId}`);
    },
  }));
}
