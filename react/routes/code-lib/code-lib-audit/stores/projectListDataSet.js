export default (organizationId, userId) => ({
  selection: false,
  primaryKey: 'id',
  paging: false,
  dataToJSON: 'all',
  transport: {
    read: () => ({
      url: `/iam/choerodon/v1/organizations/${organizationId}/users/${userId}/projects`,
      method: 'GET',
    }),
  },
  fields: [],
  queryFields: [],
});
