// 组织下已分配的权限
// eslint-disable-next-line import/no-anonymous-default-export
export default ((formatClient, formatCommon, organizationId) => ({
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
          }
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
        } catch (e) {
          return resp;
        }
      },
    }),
  },
  fields: [
    { name: 'realName', type: 'string', label: formatCommon({ id: 'username' }) },
    { name: 'syncGitlabFlag', type: 'boolean' },
    { name: 'loginName', type: 'string', label: formatCommon({ id: 'account' }) },
    { name: 'projectName', type: 'string', label: formatClient({ id: 'permission.projectName' }) },
    { name: 'repositoryName', type: 'string', label: formatClient({ id: 'permission.applicationServices' }) },
    { name: 'roleNames', type: 'string', label: formatClient({ id: 'permission.projectRoles' }) },
    {
      name: 'glAccessLevel', type: 'string', required: true, label: formatClient({ id: 'permission.permissions' }), lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    { name: 'glExpiresAt', type: 'date', label: formatClient({ id: 'permission.expirationDate' }) },
    { name: 'createdByName', type: 'string', label: formatClient({ id: 'permission.user' }) },
    { name: 'creationDate', type: 'dateTime', label: formatClient({ id: 'permission.creationTime' }) },
  ],
  queryFields: [
    { name: 'realName', type: 'string', label: formatCommon({ id: 'username' }) },
    { name: 'loginName', type: 'string', label: formatCommon({ id: 'account' }) },
    { name: 'repositoryName', type: 'string', label: formatClient({ id: 'permission.applicationServices' }) }, // 服务名称
  ],
}));
