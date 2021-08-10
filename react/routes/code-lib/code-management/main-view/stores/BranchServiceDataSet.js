export default (({ formatMessage, organizationId, projectId, setBranchApp }) => ({
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
      label: formatMessage({ id: 'infra.service' }),
    }, // 保护分支、标记的【应用服务】查询条件
  ],
  events: {
    // load: ({ dataSet }) => {
    //   dataSet.current.set('repositoryIds', dataSet.toData()[0].repositoryId);
    //   setBranchApp(dataSet.toData()[0].repositoryId);
    // },
  },
}));
