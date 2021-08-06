/* eslint-disable import/no-anonymous-default-export */
export default ({ formatMessage, intlPrefix }) => (
  {
    autoCreate: false,
    autoQuery: false,
    selection: false,
    paging: false,
    fields: [
      // 用户
      {
        name: 'userId',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.user` }),
        textField: 'realName',
        valueField: 'userId',
      },
      // 权限
      {
        name: 'glAccessLevel',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.permission` }),
        textField: 'meaning',
        valueField: 'value',
      },
      // 过期日期
      {
        name: 'glExpiresAt',
        type: 'date',
        label: formatMessage({ id: `${intlPrefix}.expiresAt` }),
      },

    ],
    // events: {
    //   create: handleCreate,
    //   update: handleUpdate,
    //   remove: handleCreate,
    // },
  }
);
