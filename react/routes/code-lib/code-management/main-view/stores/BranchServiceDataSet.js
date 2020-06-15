export default ((formatMessage) => ({
  paging: false,
  transport: {
    read: {
      data: null,
    },
  },
  fields: [
    {
      name: 'repositoryIds',
      type: 'number',
      label: formatMessage({ id: 'infra.service' }),
      // options: branchServiceDs,
      // lookupUrl: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
    }, // 服务名称
  ],
}));
