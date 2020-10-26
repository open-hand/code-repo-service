
import moment from 'moment';

export default ({ formatMessage, intlPrefix, organizationId, projectId, branchServiceDs }) => ({
  autoCreate: false,
  autoQuery: false,
  selection: false,
  paging: false,
  autoQueryAfterSubmit: false,
  transport: {
    create: ({ data: [data], dataSet }) => {
      const childrenData = dataSet.members.toData()[0];
      const members = childrenData.userId.map((v, index) => ({
        userId: v,
        glAccessLevel: childrenData.glAccessLevel[index].substring(1),
        glExpiresAt: childrenData.glExpiresAt[index] ? moment(childrenData.glExpiresAt[index]).format('YYYY-MM-DD 00:00:00') : undefined,
      }));
      const repositoryIds = (data.repositoryIds || []).map(i => i.repositoryId);
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
