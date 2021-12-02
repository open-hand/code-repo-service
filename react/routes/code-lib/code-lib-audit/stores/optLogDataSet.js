// eslint-disable-next-line import/no-anonymous-default-export
export default (formatClient, formatCommon, organizationId) => ({
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
      label: formatClient({ id: 'log.project' }),
    },
    {
      name: 'opUserName',
      type: 'string',
      label: formatClient({ id: 'log.operator' }),
    },
    {
      name: 'opType',
      type: 'string',
      label: '操作类型',
      lookupCode: 'RDUCM.OPERATION_OP_TYPE',
    },
    {
      name: 'opContent',
      type: 'string',
      label: '操作内容',
    },
    {
      name: 'opDate',
      type: 'dateTime',
      label: '操作日期',
    },
  ],
  queryFields: [
    {
      name: 'startDate',
      type: 'date',
      max: 'endDate',
      label: '开始日期',
    },
    {
      name: 'endDate',
      type: 'date',
      min: 'startDate',
      label: '结束日期',
    },
  ],
});
