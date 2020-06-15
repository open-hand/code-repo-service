const intlPrefix = 'infra.codeManage.operationLog';

export default (formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  primaryKey: 'id',
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/operation-logs`,
      method: 'get',
    }),
  },
  fields: [
    {
      name: 'repositoryId',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.repositoryId` }),
    },
    {
      name: 'opUserName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.opUserName` }),
    },
    {
      name: 'opType',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.opType` }),
      // lookupCode: 'RDUCM.OPERATION_OP_TYPE',
    },
    {
      name: 'opContent',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.opContent` }),
    },
    {
      name: 'opDate',
      type: 'dateTime',
      label: formatMessage({ id: `${intlPrefix}.model.opDate` }),
    },
  ],
  queryFields: [
    {
      name: 'opType',
      type: 'string',
    },
    {
      name: 'startDate',
      type: 'date',
      max: 'endDate',
      label: formatMessage({ id: `${intlPrefix}.model.startDate` }),
    },
    {
      name: 'endDate',
      type: 'date',
      min: 'startDate',
      label: formatMessage({ id: `${intlPrefix}.model.endDate` }),
    },
  ],
});
