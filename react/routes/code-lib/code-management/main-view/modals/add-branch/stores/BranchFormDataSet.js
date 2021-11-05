const intlPrefix = 'infra.codeManage';
// eslint-disable-next-line import/no-anonymous-default-export
export default ((formatMessage, projectId, repositoryId) => ({
  autoQuery: false,
  transport: {
    create: ({ data: editData }) => {
      const params = {
        branchName: editData[0].branchName,
        pushAccessLevel: editData[0].pushAccessLevel.substring(1), // TODO
        mergeAccessLevel: editData[0].mergeAccessLevel.substring(1), // TODO
      };
      return {
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/branches/protected-branches?branchName=${encodeURIComponent(params.branchName)}&pushAccessLevel=${encodeURIComponent(params.pushAccessLevel)}&mergeAccessLevel=${encodeURIComponent(params.mergeAccessLevel)}`,
        method: 'post',
      };
    },
  },
  fields: [
    {
      name: 'branchName',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.branch.protected` }),
      textField: 'name',
      valueField: 'name',
      // options: branchOptions,
      lookupAxiosConfig: ({ dataSet, record, params }) => ({
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/branches?excludeProtectedFlag=true`,
        method: 'get',
      }),
    }, // 受保护的分支
    {
      name: 'pushAccessLevel',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.isAllow.push` }),
      lookupCode: 'RDUCM.PT_BRANCH_ACCESS_LEVEL',
    }, // 允许合并
    {
      name: 'mergeAccessLevel',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.isAllow.merge` }),
      lookupCode: 'RDUCM.PT_BRANCH_ACCESS_LEVEL',
    }, // 允许推送
  ],
  queryFields: [],
}));
