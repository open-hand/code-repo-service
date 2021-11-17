/* eslint-disable import/no-anonymous-default-export */
export default ({
  organizationId, projectId, name = '', type,
}) => (
  {
    autoCreate: false,
    autoQuery: true,
    selection: 'single',
    paging: false,
    transport: {
      read: () => ({
        url: `/rducm/v1/${organizationId}/projects/${projectId}/c7n/members/developers`,
        params: {
          type,
          name,
        },
        method: 'GET',
      }),
    },
  });
