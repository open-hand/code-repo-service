// import { axios } from '@choerodon/boot';
import { map } from 'lodash';

export default ((intlPrefix, formatMessage, organizationId, projectId, branchServiceDs) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {
    read: () => ({
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants`,
      method: 'get',
      transformResponse: (resp) => {
        try {
          const data = JSON.parse(resp);
          if (data && data.failed) {
            return data;
          } else {
            const { list, content, ...others } = data;
            const tempList = map(data.list || data.content || [], item => ({
              ...item,
              oldAccessLevel: item.oldAccessLevel ? `L${item.oldAccessLevel}` : '',
              accessLevel: item.accessLevel ? `L${item.accessLevel}` : '',
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
    { name: 'language', defaultValue: 'zh-CN', type: 'string', label: '语言' },
    {
      name: 'applicantUser',
      type: 'object',
      label: formatMessage({ id: 'applicant' }),
    },
    {
      name: 'approvalUserName',
      type: 'string',
      label: formatMessage({ id: 'approvalUserName' }),
    },
    {
      name: 'approvalState',
      type: 'string',
      required: true,
      label: formatMessage({ id: 'status' }),
      lookupCode: 'RDUCM.APPROVAL_STATE',
      textField: 'meaning',
      valueField: 'value',
    },
    {
      name: 'repositoryName',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.service` }),
    },
    {
      name: 'applicantType',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.applyType` }),
      lookupCode: 'RDUCM.APPLICANT_TYPE',
      textField: 'meaning',
      valueField: 'value',
    },
    {
      name: 'oldAccessLevel',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.applyPermission` }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
      textField: 'meaning',
      valueField: 'value',
    },
    {
      name: 'accessLevel',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.applyPermission` }),
      lookupCode: 'RDUCM.ACCESS_LEVEL',
      textField: 'meaning',
      valueField: 'value',
    },
    {
      name: 'applicantDate',
      type: 'dateTime',
      label: formatMessage({ id: `${intlPrefix}.applyDate` }),
    },
    {
      name: 'approvalDate',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.approvalDate` }),
    },
    {
      name: 'approvalMessage',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.approvalMessage` }),
    },
    {
      name: 'expiresAt',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.expiresAt` }),
    },
  ],
  queryFields: [
    {
      name: 'applicantUserName',
      type: 'string',
      label: formatMessage({ id: 'applicant' }),
    },
    {
      name: 'approvalState',
      type: 'string',
      label: formatMessage({ id: 'status' }),
      lookupCode: 'RDUCM.APPROVAL_STATE',
      textField: 'meaning',
      valueField: 'value',
    },
    {
      name: 'repositoryIds',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.service` }),
      textField: 'repositoryName',
      valueField: 'repositoryId',
      options: branchServiceDs,
      // lookupUrl: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
    },
  ],
}));
