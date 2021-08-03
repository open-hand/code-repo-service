/* eslint-disable import/no-anonymous-default-export */
// import { axios } from '@choerodon/boot';
import { map } from 'lodash';

const intlPrefix = 'infra';

export default ((formatMessage, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/member-audit-records`,
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
      name: 'project',
      type: 'object',
      label: formatMessage({ id: 'projectName' }),
    },
    {
      name: 'repositoryName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.service` }),
    }, // 服务名称
    {
      name: 'accessLevel',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.permission` }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    {
      name: 'expiresAt',
      type: 'date',
      label: formatMessage({ id: `${intlPrefix}.expiresAt` }),
    }, // 过期日期
    {
      name: 'glAccessLevel',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.glAccessLevel` }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    {
      name: 'glExpiresAt',
      type: 'date',
      label: formatMessage({ id: `${intlPrefix}.glExpiresAt` }),
    }, // 过期日期
    {
      name: 'syncGitlabFlag',
      type: 'boolean',
    }, // 是否已同步
  ],
  queryFields: [
    // {
    //   name: 'realName',
    //   type: 'string',
    //   label: formatMessage({ id: 'userName' }),
    // },
    // {
    //   name: 'loginName',
    //   type: 'string',
    //   label: formatMessage({ id: 'loginName' }),
    // },
    {
      name: 'repositoryName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.service` }),
    }, // 服务名称
  ],
}));
