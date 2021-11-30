// import { axios } from '@choerodon/boot';
import map from 'lodash/map';
// import getTablePostData from '@/utils/getTablePostData';
const intlPrefix = 'infra.codeManage';
export default ((formatMessage, projectId, repositoryId, format) => ({
  autoQuery: false,
  selection: false,
  paging: false,
  transport: {
    read: () => ({
      url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/branches/protected-branches`,
      method: 'get',
      transformResponse: (resp) => {
        try {
          const data = JSON.parse(resp);
          if (data && data.failed) {
            return data;
          }
          return map(data, item => ({
            ...item,
            pushAccessLevel: `L${item.pushAccessLevels[0].accessLevel}`,
            mergeAccessLevel: `L${item.mergeAccessLevels[0].accessLevel}`,
          }));
        } catch (e) {
          return resp;
        }
      },
    }),
    update: ({ data: editData }) => {
      const params = {
        branchName: editData[0].name,
        pushAccessLevel: editData[0].pushAccessLevel.substring(1), // TODO
        mergeAccessLevel: editData[0].mergeAccessLevel.substring(1), // TODO
      };
      return {
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/branches/protected-branches?branchName=${encodeURIComponent(params.branchName)}&pushAccessLevel=${encodeURIComponent(params.pushAccessLevel)}&mergeAccessLevel=${encodeURIComponent(params.mergeAccessLevel)}`,
        method: 'put',
      };
    },
    destroy: ({ data: deleteData }) => {
      const branchName = deleteData[0].name;
      return {
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/branches/protected-branches?branchName=${encodeURIComponent(branchName)}`,
        method: 'delete',
      };
    },
  },
  fields: [
    {
      name: 'name',
      type: 'string',
      required: true,
      label: format({ id: 'ProtectedBranch' }),
      // textField: 'name',
      // valueField: 'name',
    }, // 受保护的分支
    {
      name: 'pushAccessLevel',
      type: 'string',
      required: true,
      label: format({ id: 'AllowedPush' }),
      lookupCode: 'RDUCM.PT_BRANCH_ACCESS_LEVEL',
    }, // 允许合并
    {
      name: 'mergeAccessLevel',
      type: 'string',
      required: true,
      label: format({ id: 'AllowedMerge' }),
      lookupCode: 'RDUCM.PT_BRANCH_ACCESS_LEVEL',
    }, // 允许推送
  ],
  queryFields: [],
}));
