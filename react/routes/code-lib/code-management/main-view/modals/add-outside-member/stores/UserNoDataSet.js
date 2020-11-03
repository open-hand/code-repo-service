export default ({ organizationId, projectId }) => ({
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/rducm/v1/${organizationId}/projects/${projectId}/c7n/non-project-members`,
      method: 'get',
      params: {
        name: '',
      },
    },
  },
  fields: [
    { name: 'realName', type: 'string' },
    { name: 'loginName', type: 'string' },
    { name: 'userId', type: 'string', unique: true },
  ],
});
