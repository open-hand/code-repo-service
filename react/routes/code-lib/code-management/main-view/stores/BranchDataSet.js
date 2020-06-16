// import { axios } from '@choerodon/boot';
import map from 'lodash/map';
// import getTablePostData from '@/utils/getTablePostData';
const intlPrefix = 'infra.codeManage';
export default ((formatMessage, projectId, repositoryId) => ({
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
          } else {
            return map(data, item => ({
              ...item,
              pushAccessLevel: `L${item.pushAccessLevels[0].accessLevel}`,
              mergeAccessLevel: `L${item.mergeAccessLevels[0].accessLevel}`,
            }));
          }
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
      label: formatMessage({ id: `${intlPrefix}.branch.protected` }),
      // textField: 'name',
      // valueField: 'name',
      // lookupUrl: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/branches?excludeProtectedFlag=true`,
    }, // 受保护的分支
    {
      name: 'pushAccessLevel',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.allow.push` }),
      lookupCode: 'RDUCM.PT_BRANCH_ACCESS_LEVEL',
    }, // 允许合并
    {
      name: 'mergeAccessLevel',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.allow.merge` }),
      lookupCode: 'RDUCM.PT_BRANCH_ACCESS_LEVEL',
    }, // 允许推送
  ],
  queryFields: [],
}));
