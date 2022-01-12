/* eslint-disable import/no-anonymous-default-export */
import { DataSet } from 'choerodon-ui/pro';

export default (({
  formatMessage, organizationId, projectId, setBranchApp, format,
}) => ({
  paging: false,
  autoCreate: true,
  // autoQuery: true,
  fields: [
    {
      name: 'repositoryIds',
      type: 'object',
      label: format({ id: 'ApplicationService' }),
      textField: 'repositoryName',
      valueField: 'repositoryId',
      options: new DataSet({
        autoQuery: true,
        paging: false,
        transport: {
          read: {
            url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
            method: 'get',
            transformResponse: (resp) => {
              try {
                const data = JSON.parse(resp);
                data.unshift({
                  repositoryId: 'all',
                  externalConfigId: null,
                  repositoryName: '全部应用服务',
                });
                return data;
              } catch (e) {
                return resp;
              }
            },
          },
        },
      }),
    }, // 保护分支、标记的【应用服务】查询条件
  ],
  events: {
    // load: ({ dataSet }) => {
    //   dataSet.current.set('repositoryIds', dataSet.toData()[0].repositoryId);
    //   setBranchApp(dataSet.toData()[0].repositoryId);
    // },
  },
}));
