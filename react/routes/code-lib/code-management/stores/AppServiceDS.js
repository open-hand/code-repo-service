export default ({ organizationId, projectId }) => ({
  paging: false,
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
      method: 'get',
    }),
  },
});
