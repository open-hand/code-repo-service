export default (intlPrefix, formatMessage, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  primaryKey: 'id',
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/gitlab/repositories/operation-logs`,
      method: 'GET',
    }),
  },
  fields: [
    {
      name: 'projectId',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.projectId` }),
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
      lookupCode: 'RDUCM.OPERATION_OP_TYPE',
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
