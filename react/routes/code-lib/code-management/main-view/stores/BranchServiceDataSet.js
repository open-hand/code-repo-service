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
      type: 'string',
      label: formatMessage({ id: 'infra.service' }),
    }, // 保护分支、标记的【应用服务】查询条件
  ],
}));
