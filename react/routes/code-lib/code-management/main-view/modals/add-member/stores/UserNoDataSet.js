export default ({ organizationId, projectId }) => ({
  autoCreate: false,
  autoQuery: true,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/rducm/v1/${organizationId}/projects/${projectId}/c7n/members/developers`,
      method: 'get',
    },
  },
});
