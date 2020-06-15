export default ((intlPrefix, formatMessage, projectId, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 18,
  transport: {
    read: () => ({
      url: `/rduem/v1/cluster-application-forms-query/${projectId}/list`,
      method: 'GET',
    }),
    destroy: ({ data: [data] }) => ({
      url: `/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/${data.id}`,
      method: 'delete',
    }),
  },
  fields: [
    {
      name: 'applicationNo',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.applicationNo` }),
    },
    {
      name: 'state',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.applyStatus` }),
    },
    {
      name: 'applicationDate',
      type: 'dataTime',
      label: formatMessage({ id: `${intlPrefix}.model.applicationDate` }),
    },
    {
      name: 'applicationPurpose',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.applicationPurpose` }),
    },
    {
      name: 'activityName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.currentNode` }), // 节点名称
    },
  ],
  queryFields: [
    // {
    //   name: 'code',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.repoCode` }),
    //   required: true,
    //   textField: 'code',
    //   valueField: 'code',
    //   lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    // },
    // {
    //   name: 'name',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.mirrorLibName` }),
    //   required: true,
    //   textField: 'name',
    //   valueField: 'name',
    //   lookupUrl: `/rdupm/v1/harbor-project/all/${organizationId}`,
    // },
    // {
    //   name: 'imageName',
    //   type: 'string',
    //   label: formatMessage({ id: `${intlPrefix}.model.imageName` }),
    // },
  ],
}));
