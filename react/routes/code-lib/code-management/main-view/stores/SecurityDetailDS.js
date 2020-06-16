export default function ({ formatMessage, intlPrefix, userId, projectId, organizationId }) {
  return {
    autoQuery: true,
    selection: false,
    pageSize: 10,
    transport: {
      read: () => ({
        url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/members/${userId}/permissions`,
        method: 'get',
        transformResponse: (resp) => {
          try {
            const data = JSON.parse(resp);
            if (data && data.failed) {
              return data;
            } else {
              const { list, content, ...others } = data;
              const tempList = (data.list || data.content || []).map(item => ({
                ...item,
                glAccessLevel: item.glAccessLevel ? `L${item.glAccessLevel}` : '',
              }));
              return {
                ...others,
                list: tempList,
                content: tempList,
              };
            }
          } catch (e) {
            return resp;
          }
        },
      }),
    },
    fields: [{
      name: 'repositoryName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.service` }),
    }, {
      name: 'glAccessLevel',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.permission` }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    {
      name: 'glExpiresAt',
      type: 'date',
      label: formatMessage({ id: `${intlPrefix}.expiresAt` }),
    },
    {
      name: 'lastUpdateDate',
      type: 'dateTime',
      label: formatMessage({ id: `${intlPrefix}.lastUpdateDate` }),
    }],
  };
}
