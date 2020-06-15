import omit from 'lodash/omit';

export default ({ formatMessage, intlPrefix, pathListDs, organizationId, projectId, branchServiceDs }) => ({
  autoCreate: false,
  autoQuery: false,
  selection: false,
  paging: false,
  autoQueryAfterSubmit: false,
  children: {
    members: pathListDs,
  },
  transport: {
    create: ({ data: [data] }) => {
      const postData = omit(data, '__id', '__status');
      const repositoryIds = (data.repositoryIds || []).map(i => i.repositoryId);
      const members = (postData.members || []).map(i => ({ userId: i.userId, glAccessLevel: i.glAccessLevel.substring(1), glExpiresAt: i.glExpiresAt }));
      const params = {
        allMemberFlag: false,
        repositoryIds,
        members,
      };
      return ({
        url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/batch-add`,
        method: 'post',
        data: params,
      });
    },
  },
  fields: [
    // 应用服务
    {
      name: 'repositoryIds',
      type: 'object',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.service` }),
      textField: 'repositoryName',
      valueField: 'repositoryId',
      options: branchServiceDs,
    },
  ],
  // events: {
  //   load: handleLoad,
  //   update: handleUpdate,
  // },
});
