/* eslint-disable max-len */
/* eslint-disable import/no-anonymous-default-export */
import omit from 'lodash/omit';
import { DataSet } from 'choerodon-ui/pro';

const permissionsLevelDataSet = new DataSet({
  fields: [{ name: 'text', type: 'string' }, { name: 'value', type: 'string' }],
  data: [
    {
      text: '应用服务',
      value: 'applicationService',
    },
    {
      text: '项目全局',
      value: 'allProject',
    },
  ],
});
export default ({
  formatMessage,
  intlPrefix,
  pathListDs,
  organizationId,
  projectId,
  branchServiceDs,
  currentBranchAppId,
}) => ({
  autoCreate: false,
  autoQuery: false,
  selection: false,
  paging: false,
  autoQueryAfterSubmit: false,
  children: {
    members: pathListDs,
  },
  transport: {
    create: ({ data: [data], parms, dataSet }) => {
      const postData = omit(data, '__id', '__status');
      const repositoryIds = (data.repositoryIds || []).map(i => i.repositoryId);
      const members = (postData.members || []).map(i => ({
        userId: i.userId,
        glAccessLevel: i.glAccessLevel.substring(1),
        glExpiresAt: i.glExpiresAt,
      }));
      const permissionValue = dataSet.current.get('permissionsLevel');
      const url = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/batch-add`;
      if (permissionValue === 'applicationService') { // 应用服务
        return {
          url,
          method: 'post',
          data: {
            allMemberFlag: false,
            repositoryIds,
            members,
          },
        };
      }
      return {// 项目全局
        url: `${url}/group`,
        method: 'post',
        data: members,
      };
    },
  },
  fields: [
    {
      name: 'permissionsLevel',
      type: 'string',
      label: '权限层级',
      textField: 'text',
      valueField: 'value',
      options: permissionsLevelDataSet,
      defaultValue: 'allProject',
    },
    // 应用服务
    {
      name: 'repositoryIds',
      type: 'object',
      label: formatMessage({ id: `${intlPrefix}.service` }),
      textField: 'repositoryName',
      valueField: 'repositoryId',
      options: branchServiceDs,
      defaultValue: branchServiceDs.find(record => record.get('repositoryId') === currentBranchAppId)
        ? [
            branchServiceDs
              .find(record => record.get('repositoryId') === currentBranchAppId)
              ?.toData(),
        ]
        : [],
      computedProps: {
        ignore: ({ dataSet }) =>
          (dataSet.current.get('permissionsLevel') === 'applicationService'
            ? 'never'
            : 'always'),
        required: ({ dataSet }) =>
          (dataSet.current.get('permissionsLevel') === 'applicationService'),
      },
    },
  ],
});
