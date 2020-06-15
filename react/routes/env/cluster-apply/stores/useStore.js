import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    // 原有申请单数据
    oldOptsRecord: [],
    setOldOptsRecord(data) {
      this.oldOptsRecord = data || [];
    },
    get getOldOptsRecord() {
      return this.oldOptsRecord;
    },
    // 加载更多按钮
    isMore: false,
    setLoadMoreBtn(data) {
      this.isMore = data;
    },
    get getLoadMoreBtn() {
      return this.isMore;
    },

    // 删除单据
    deleteDocument(data) {
      return axios.delete(`/rduem/v1/${data.organizationId}/projects/${data.projectId}/clusters/application-forms/${data.id}`);
    },
    // 保存单据
    saveDocument(data) {
      return axios.post(`/rduem/v1/${data.organizationId}/projects/${data.projectId}/clusters/application-forms/${data.id}`);
    },
  }));
}
