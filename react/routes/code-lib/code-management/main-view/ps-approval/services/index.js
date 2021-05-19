import ApproveApis from '@/routes/code-lib/code-management/main-view/ps-approval/apis';
import { axios } from '@choerodon/master';

export default class BatchApproveServices {
  static axiosPostBatchPass(orgId, proId, expiresAt, data) {
    return axios.post(ApproveApis.getBatchPassUrl(orgId, proId, expiresAt), data);
  }

  static axiosPostBatchReject(orgId, proId, approvalMessage, data) {
    return axios.post(ApproveApis.getBatchRejectUrl(orgId, proId, approvalMessage), data);
  }
}
