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
  const setRequired = (DS, bool, fieldNameArr) => {
    DS.created.forEach((item) => {
      fieldNameArr.forEach((name) => {
        if (item.getField(name)) {
          item.getField(name).set('required', bool);
        }
      });
    });
  };
  const requireChange = (value) => {
    if (value === 'permission') {
      setRequired(pathListDs, false, ['glAccessLevel', 'userId']);
      setRequired(UserPathListDS, true, ['userId']);
    }
    if (value === 'simple') {
      setRequired(pathListDs, true, ['glAccessLevel', 'userId']);
      setRequired(UserPathListDS, false, ['userId']);
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
        options: branchServiceDs?.current?.getField('repositoryIds')?.options,
        defaultValue: branchServiceDs?.current?.getField('repositoryIds')?.options.find(record => record.get('repositoryId') === currentBranchAppId) && currentBranchAppId !== 'all'
          ? [
            branchServiceDs?.current?.getField('repositoryIds')?.options
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
        computedProps: {
          ignore: ({ dataSet }) =>
            (dataSet.current.get('addingMode') === 'permission'
              ? 'never'
              : 'always'),
          required: ({ dataSet }) => dataSet.current.get('addingMode') === 'permission',
        },
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
          requireChange(value, dataSet);
        }
        if (name === 'glAccessLevel') {
          UserPathListDS.created.forEach((item) => {
            item.reset();
          });
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

        if (addingMode === 'simple') {
          members = (postData.members || []).map(i => ({
            userId: i.userId,
            glAccessLevel: i.glAccessLevel.substring(1),
            glExpiresAt: i.glExpiresAt,
          }));
        }
        if (addingMode === 'permission') {
          members = (postData.permissionMembers || []).map(i => ({
            userId: i.userId,
            glAccessLevel: Number(dataSet?.current?.get('glAccessLevel')?.substring(1)),
            glExpiresAt: dataSet?.current?.get('glExpiresAt'),
          }));
        }

        const url = `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/batch-add`;

        if (permissionValue === 'applicationService') { // 应用服务添加
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

        if (permissionValue === 'allProject') {
          return {// 项目全局普通添加
            url: `${url}/group`,
            method: 'post',
            data: members,
          };
        }
        return true;
      },
    },
  };
};
