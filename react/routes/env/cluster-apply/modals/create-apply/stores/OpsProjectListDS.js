export default ({ organizationId, projectId }) => ({
  autoCreate: false,
  autoQuery: true,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/ops-projects`,
      method: 'get',
    },
  },
});
