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
      url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/tags/protected-tags`,
      method: 'get',
      transformResponse: (resp) => {
        try {
          const data = JSON.parse(resp);
          if (data && data.failed) {
            return data;
          }
          return map(data, item => ({
            ...item,
            createAccessLevel: `L${item.createAccessLevels[0].accessLevel}`,
          }));
        } catch (e) {
          return resp;
        }
      },
    }),
    update: ({ data: editData }) => {
      const params = {
        tagName: editData[0].name,
        createAccessLevel: editData[0].createAccessLevel.substring(1), // TODO
      };
      return {
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/tags/protected-tags?tagName=${encodeURIComponent(params.tagName)}&createAccessLevel=${encodeURIComponent(params.createAccessLevel)}`,
        method: 'put',
        transformRequest: (() => JSON.stringify(params)),
      };
    },
    destroy: ({ data: deleteData }) => {
      const tagName = deleteData[0].name;
      return {
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/tags/protected-tags?tagName=${encodeURIComponent(tagName)}`,
        method: 'delete',
      };
    },
  },
  fields: [
    {
      name: 'name',
      type: 'string',
      required: true,
      label: format({ id: 'ProtectedTag' }),
    }, // 受保护的标记
    {
      name: 'createAccessLevel',
      type: 'string',
      required: true,
      label: format({ id: 'AllowedCreate' }),
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUCM.PT_TAG_ACCESS_LEVEL',
    }, // 允许创建
  ],
  queryFields: [],
}));
