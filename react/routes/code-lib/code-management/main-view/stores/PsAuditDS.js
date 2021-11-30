/* eslint-disable import/no-anonymous-default-export */
// import { axios } from '@choerodon/boot';
import { map } from 'lodash';
import Apis from '../../apis';

export default ((intlPrefix, formatMessage, organizationId, projectId, branchAppId, format) => ({
  autoQuery: false,
  selection: 'multiple',
  pageSize: 10,
  transport: {
    read: () => ({
      url: Apis.getAuditUrl(organizationId, projectId, branchAppId),
      method: 'get',
      transformResponse: (resp) => {
        try {
          const data = JSON.parse(resp);
          if (data && data.failed) {
            return data;
          }
          const { list, content, ...others } = data;
          const tempList = map(data.list || data.content || [], item => ({
            ...item,
            accessLevel: item.accessLevel ? `L${item.accessLevel}` : '-',
            glAccessLevel: item.glAccessLevel ? `L${item.glAccessLevel}` : '-',
          }));
          return {
            ...others,
            list: tempList,
            content: tempList,
          };
        } catch (e) {
          return resp;
        }
      },
    }),
  },
  fields: [
    {
      name: 'realName',
      type: 'string',
      label: formatMessage({ id: 'userName' }),
    }, // 用户名
    {
      name: 'loginName',
      type: 'string',
      label: formatMessage({ id: 'loginName' }),
    }, // 登录名
    {
      name: 'repositoryName',
      type: 'string',
      label: format({ id: 'ApplicationService' }),
    }, // 服务名称
    {
      name: 'accessLevel',
      type: 'string',
      label: format({ id: 'Permission' }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    {
      name: 'expiresAt',
      type: 'date',
      label: format({ id: 'ExpirationDate' }),
    }, // 过期日期
    {
      name: 'glAccessLevel',
      type: 'string',
      label: format({ id: 'PermissionGitLab' }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    {
      name: 'glExpiresAt',
      type: 'date',
      label: format({ id: 'ExpirationDateGitLab' }),
    }, // 过期日期
    {
      name: 'syncGitlabFlag',
      type: 'boolean',
    }, // 是否已同步
  ],
}));
