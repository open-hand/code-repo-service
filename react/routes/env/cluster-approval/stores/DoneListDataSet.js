export default ((intlPrefix, formatMessage, projectId) => ({
  autoQuery: false,
  selection: false,
  page: 10,
  transport: {
    read: () => ({
      url: `/rduem/v1/cluster-application-forms-query/${projectId}/list/approve/his`,
      method: 'GET',
    }),
  },
  fields: [
    { name: 'applicationNo', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.applicationNo`, defaultMessage: '申请单编号' }) },
    {
      name: 'state',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.state`, defaultMessage: '状态' }),
      lookupCode: 'RDUEM.CAF_STATE',
    },
    {
      name: 'applicationPurpose',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.applicationPurpose`, defaultMessage: '用途' }),
      lookupCode: 'RDUEM.CAF_PURPOSE',
    },
    { name: 'projectName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.projectName`, defaultMessage: '项目' }) },
    { name: 'user', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.user`, defaultMessage: '申请人' }) },
    { name: 'applicationDate', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.applicationDate`, defaultMessage: '申请时间' }) },
  ],
  queryFields: [],
}));
