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

const addingModeDataSet = new DataSet({
  fields: [{ name: 'text', type: 'string' }, { name: 'value', type: 'string' }],
  data: [
    {
      text: '普通添加',
      value: 'simple',
    },
    {
      text: '基于权限添加',
      value: 'permission',
    },
  ],
});

export default ({
  formatMessage,
  intlPrefix,
  pathListDs,
  UserPathListDS,
  organizationId,
  projectId,
  branchServiceDs,
  currentBranchAppId,
}) => {
  const requireChange = (value) => {
    if (value === 'permission') {
      pathListDs.created.forEach((item) => {
        item.getField('glAccessLevel').set('required', false);
        item.getField('userId').set('required', false);
      });
      UserPathListDS.created.forEach((item) => {
        item.getField('userId').set('required', true);
      });
    }
    if (value === 'simple') {
      pathListDs.created.forEach((item) => {
        item.getField('glAccessLevel').set('required', true);
        item.getField('userId').set('required', true);
      });
      UserPathListDS.created.forEach((item) => {
        item.getField('userId').set('required', false);
      });
    }
  };
  return {
    autoCreate: false,
    autoQuery: false,
    selection: false,
    paging: false,
    autoQueryAfterSubmit: false,
    children: {
      members: pathListDs,
      permissionMembers: UserPathListDS,
    },
    fields: [
      {
        name: 'permissionsLevel',
        type: 'string',
        label: '权限层级',
        textField: 'text',
        valueField: 'value',
        options: permissionsLevelDataSet,
        defaultValue: 'applicationService',
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
      // 添加方式
      {
        name: 'addingMode',
        type: 'string',
        label: '添加方式',
        textField: 'text',
        valueField: 'value',
        options: addingModeDataSet,
        defaultValue: 'simple',
      },


      // 权限
      {
        name: 'glAccessLevel',
        type: 'string',
        label: '权限',
        textField: 'meaning',
        valueField: 'value',
        lookupCode: 'RDUCM.ACCESS_LEVEL',
        ignore: ({ dataSet }) =>
          (dataSet.current.get('addingMode') === 'permission'
            ? 'never'
            : 'always'),
        required: ({ dataSet }) =>
          (dataSet.current.get('addingMode') === 'permission'),
      },
      // 过期日期
      {
        name: 'glExpiresAt',
        type: 'date',
        label: '过期日期',
        ignore: ({ dataSet }) =>
          (dataSet.current.get('addingMode') === 'permission'
            ? 'never'
            : 'always'),
      },
    ],
    // pathListDs ，UserPathListDS
    events: {
      update: ({
        dataSet, record, name, value, oldValue,
      }) => {
        if (name === 'permissionsLevel') {
          pathListDs.created.forEach((item) => {
            item.reset();
          });
          UserPathListDS.created.forEach((item) => {
            item.reset();
          });
        }
        if (name === 'addingMode') {
          requireChange(value);
        }
      },
    },
    transport: {
      create: ({ data: [data], parms, dataSet }) => {
        const postData = omit(data, '__id', '__status');
        const repositoryIds = (data.repositoryIds || []).map(i => i.repositoryId);
        const permissionValue = dataSet.current.get('permissionsLevel');
        const addingMode = dataSet.current.get('addingMode');

        let members;
        let permissionMembers;

        if (addingMode === 'simple') {
          members = (postData.members || []).map(i => ({
            userId: i.userId,
            glAccessLevel: i.glAccessLevel.substring(1),
            glExpiresAt: i.glExpiresAt,
          }));
        }
        if (addingMode === 'permission') {
          permissionMembers = (postData.permissionMembers || []).map(i => ({
            userId: i.userId,
          }));
        }

        const url = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/batch-add`;

        if (permissionValue === 'applicationService' && addingMode === 'simple') { // 应用服务普通添加
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
        if (permissionValue === 'applicationService' && addingMode === 'permission') { // 应用服务基于权限添加
          return {
            url,
            method: 'post',
            data: {
              allMemberFlag: false,
              repositoryIds,
              members: permissionMembers,
              baseRole: true,
              glAccessLevel: dataSet.current.get('glAccessLevel'),
              glExpiresAt: dataSet.current.get('glExpiresAt'),
            },
          };
        }

        if (permissionValue === 'allProject' && addingMode === 'simple') {
          return {// 项目全局
            url: `${url}/group`,
            method: 'post',
            data: members,
          };
        }
        if (permissionValue === 'allProject' && addingMode === 'permission') {
          return {// 项目全局
            url: `${url}/group`,
            method: 'post',
            data: {
              baseRole: true,
              glAccessLevel: dataSet.current.get('glAccessLevel'),
              glExpiresAt: dataSet.current.get('glExpiresAt'),
              members: permissionMembers,
            },
          };
        }
        return true;
      },
    },
  };
};
