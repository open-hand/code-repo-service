export default (projectId, branchAppId) => ({
  autoCreate: false,
  autoQuery: true,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${branchAppId}/tags?excludeProtectedFlag=true`,
      method: 'get',
    },
  },
});
