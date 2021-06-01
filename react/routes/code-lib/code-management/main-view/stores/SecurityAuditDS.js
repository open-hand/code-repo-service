import React from 'react';
import Tips from '@/components/new-tips';

export default ((intlPrefix, formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/audit/security-audit`,
      method: 'get',
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
    },
    {
      name: 'roleNames',
      type: 'string',
      label: formatMessage({ id: 'projectRole' }),
    }, // 项目角色
    {
      name: 'authorizedRepositoryCount',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.authorizedCount` }),
    },
    {
      name: 'allRepositoryCount',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.allCount` }),
    },
    {
      name: 'authorizedRate',
      type: 'string',
      // eslint-disable-next-line react/react-in-jsx-scope
    }],
  queryFields: [
    { name: 'realName', type: 'string', label: formatMessage({ id: 'userName' }) },
    { name: 'loginName', type: 'string', label: formatMessage({ id: 'loginName' }) },
  ],
}));
