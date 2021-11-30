export default (({
  formatMessage, organizationId, projectId, setBranchApp, format,
}) => ({
  paging: false,
  autoQuery: true,
  transport: {
    read: {
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
      method: 'get',
    },
  },
  fields: [
    {
      name: 'repositoryIds',
      type: 'string',
      label: format({ id: 'ApplicationService' }),
    }, // 保护分支、标记的【应用服务】查询条件
  ],
  events: {
    // load: ({ dataSet }) => {
    //   dataSet.current.set('repositoryIds', dataSet.toData()[0].repositoryId);
    //   setBranchApp(dataSet.toData()[0].repositoryId);
    // },
  },
}));
