import omit from 'lodash/omit';

export default ({ formatMessage, intlPrefix, organizationId, projectId, userId, realName, branchServiceDs }) => ({
  autoCreate: false,
  autoQuery: false,
  selection: false,
  paging: false,
  autoQueryAfterSubmit: false,
  transport: {
    create: ({ data: [data] }) => {
      const postData = omit(data, '__id', '__status');
      const params = {
        accessLevel: postData.accessLevel.substring(1),
        applicantType: postData.applicantType,
        repositoryId: postData.repositoryId,
        applicantUserId: userId,
      };
      return ({
        url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/member-applicants`,
        method: 'post',
        data: params,
      });
    },
  },
  fields: [
    {
      name: 'applicantUserName',
      type: 'string',
      required: true,
      defaultValue: realName,
      label: formatMessage({ id: 'userName' }),
    },
    {
      name: 'repositoryId',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.service` }),
      textField: 'repositoryName',
      valueField: 'repositoryId',
      options: branchServiceDs,
    },
    {
      name: 'applicantType',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.applyType` }),
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUCM.APPLICANT_TYPE',
    },
    {
      name: 'oldAccessLevel',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.applyPermission` }),
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },
    {
      name: 'accessLevel',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.applyPermission` }),
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUCM.ACCESS_LEVEL',
    },

  ],
  // events: {
  //   load: handleLoad,
  //   update: handleUpdate,
  // },
});
