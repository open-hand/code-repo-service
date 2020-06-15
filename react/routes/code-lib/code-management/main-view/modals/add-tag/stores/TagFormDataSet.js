
const intlPrefix = 'infra.codeManage';
export default ((formatMessage, projectId, repositoryId, tagOptions) => ({
  autoQuery: false,
  transport: {
    create: ({ data: editData }) => {
      const params = {
        tagName: editData[0].tagName,
        createAccessLevel: editData[0].createAccessLevel.substring(1),
      };
      return {
        url: `/rducm/v1/projects/${projectId}/gitlab/repositories/${repositoryId}/tags/protected-tags?tagName=${encodeURIComponent(params.tagName)}&createAccessLevel=${encodeURIComponent(params.createAccessLevel)}`,
        method: 'post',
      };
    },
  },
  fields: [
    {
      name: 'tagName',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.tag.protected` }),
      textField: 'name',
      valueField: 'name',
      options: tagOptions,
    }, // 受保护的标记
    {
      name: 'createAccessLevel',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.isAllow.create` }),
      defaultValue: 'L40',
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUCM.PT_TAG_ACCESS_LEVEL',
    }, // 允许创建
  ],
  queryFields: [],
}));
