import omit from 'lodash/omit';

export default ({ formatMessage, intlPrefix, applicationListDs, organizationId, projectId, realName, opsProjectList, id }) => ({
  autoCreate: false,
  autoQuery: false,
  selection: false,
  paging: false,
  autoQueryAfterSubmit: false,
  children: {
    applicationList: applicationListDs,
  },
  transport: {
    read: () => ({
      url: `/rduem/v1/cluster-application-forms-query/${projectId}/${id}`,
      method: 'get',
    }),
    create: ({ data: [data] }) => {
      const postData = omit(data, '__id', '__status');
      const params = {
        ...postData,
        applicationList: postData.applicationList[0],
      };
      return ({
        url: `/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/save`,
        method: 'post',
        data: params,
      });
    },
    update: ({ data: [data] }) => {
      const params = {
        ...data,
        applicationList: data.applicationList[0],
      };
      return ({
        url: `/rduem/v1/${organizationId}/projects/${projectId}/clusters/application-forms/save`,
        method: 'post',
        data: params,
      });
    },
  },
  fields: [
    {
      name: 'applicantUserName',
      type: 'string',
      defaultValue: realName,
      label: formatMessage({ id: 'infra.env.approval.model.user' }),
    },
    {
      name: 'applicationDate',
      type: 'date',
      defaultValue: new Date(),
      label: formatMessage({ id: `${intlPrefix}.model.applicationDate` }),
    },
    {
      name: 'opsProjectId',
      type: 'number',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.opsProject` }),
      textField: 'name',
      valueField: 'id',
      options: opsProjectList,

    },
    {
      name: 'applicationPurpose',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.applicationPurpose` }),
      textField: 'meaning',
      valueField: 'value',
      lookupCode: 'RDUEM.CAF_PURPOSE',
    },
    {
      name: 'deadlineDate',
      type: 'date',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.deadlineDate` }),
    },
    {
      name: 'applicationDesc',
      type: 'string',
      required: true,
      label: formatMessage({ id: 'description' }),
    },
  ],
});
