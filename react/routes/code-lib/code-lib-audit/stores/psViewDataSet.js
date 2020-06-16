// 组织下已分配的权限
export default ((intlPrefix, formatMessage, organizationId) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/gitlab/repositories/members`,
      method: 'GET',
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
  fields: [
    { name: 'realName', type: 'string', label: formatMessage({ id: 'userName' }) },
    { name: 'syncGitlabFlag', type: 'boolean' },
    { name: 'loginName', type: 'string', label: formatMessage({ id: 'loginName' }) },
    { name: 'projectName', type: 'string', label: formatMessage({ id: 'projectName' }) },
    { name: 'repositoryName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.service` }) },
    { name: 'roleNames', type: 'string', label: formatMessage({ id: 'projectRole' }) },
    { name: 'glAccessLevel', type: 'string', required: true, label: formatMessage({ id: `${intlPrefix}.model.permission` }), lookupCode: 'RDUCM.ACCESS_LEVEL' },
    { name: 'glExpiresAt', type: 'date', label: formatMessage({ id: `${intlPrefix}.model.glExpiresAt` }) },
    { name: 'createdByName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.creationBy` }) },
    { name: 'creationDate', type: 'dateTime', label: formatMessage({ id: 'addTime' }) },
  ],
  queryFields: [
    { name: 'realName', type: 'string', label: formatMessage({ id: 'userName' }) },
    { name: 'loginName', type: 'string', label: formatMessage({ id: 'loginName' }) },
    { name: 'repositoryName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.service` }) }, // 服务名称
  ],
}));
