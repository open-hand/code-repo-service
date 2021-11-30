/* eslint-disable max-len */
/* eslint-disable import/no-anonymous-default-export */

import React from 'react';
import Tips from '@/components/new-tips';

const intlPrefix = 'infra.codeManage';

export default ((formatMessage, organizationId, projectId, branchServiceDs, format) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/overview`,
      method: 'get',
    }),
  },
  fields: [
    {
      name: 'repositoryName',
      type: 'string',
      label: format({ id: 'ServiceName' }),
    },
    {
      name: 'managerCount',
      type: 'number',
      label: <Tips title={format({ id: 'ManagementMembers' })} helpText={formatMessage({ id: `${intlPrefix}.develop.managerCountTips` })} />,
    },
    {
      name: 'developerCount',
      type: 'number',
      label: format({ id: 'DevelopmentMembers' }),
    },
    {
      name: 'defaultBranch',
      type: 'string',
      label: format({ id: 'DefaultBranch' }),
    },
    {
      name: 'visibility',
      type: 'string',
      label: format({ id: 'Visibility' }),
    },
    {
      name: 'lastCommittedDate',
      type: 'dateTime',
      label: format({ id: 'LastCommit' }),
    },
    {
      name: 'openedMergeRequestCount',
      type: 'number',
      label: format({ id: 'MergeRequests' }),
    },
    {
      name: 'repositoryCreationDate',
      type: 'dateTime',
      label: format({ id: 'CreationDate' }),
    },
  ],
  queryFields: [
    {
      name: 'repositoryIds',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.repositoryName` }),
      textField: 'repositoryName',
      valueField: 'repositoryId',
      options: branchServiceDs,
      // lookupUrl: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
    },
  ],
}));
