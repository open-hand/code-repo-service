import { useLocalStore } from 'mobx-react-lite';
import omit from 'lodash/omit';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    // 提交单据
    commitDocument(data) {
      const { organizationId, projectId, postData } = data;
      const tempData = omit(postData[0], '__id', '__status');
      const body = {
        ...tempData,
        applicationList: tempData.applicationList[0] || [],
      };
      return axios.post(`/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/save-and-submit`, JSON.stringify(body));
    },
  }));
}
