/* eslint-disable max-len */
/* eslint-disable import/no-anonymous-default-export */

import React from 'react';
import Tips from '@/components/new-tips';

const intlPrefix = 'infra.codeManage';

export default ((formatMessage, organizationId, projectId, branchServiceDs) => ({
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
      label: formatMessage({ id: `${intlPrefix}.repositoryName` }),
    },
    {
      name: 'managerCount',
      type: 'number',
      label: <Tips title={formatMessage({ id: `${intlPrefix}.develop.managerCount` })} helpText={formatMessage({ id: `${intlPrefix}.develop.managerCountTips` })} />,
    },
    {
      name: 'developerCount',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.develop.allMember` }),
    },
    {
      name: 'defaultBranch',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.default.branch` }),
    },
    {
      name: 'visibility',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.lib.visible` }),
    },
    {
      name: 'lastCommittedDate',
      type: 'dateTime',
      label: formatMessage({ id: `${intlPrefix}.last.commit` }),
    },
    {
      name: 'openedMergeRequestCount',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.wait.handle.number` }),
    },
    {
      name: 'repositoryCreationDate',
      type: 'dateTime',
      label: formatMessage({ id: `${intlPrefix}.creationDate` }),
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
