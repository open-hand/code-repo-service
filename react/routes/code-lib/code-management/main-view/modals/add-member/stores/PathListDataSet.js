export default ({ formatMessage, intlPrefix, userOptions }) => (
  {
    autoCreate: false,
    autoQuery: false,
    selection: false,
    paging: false,
    fields: [
      // 用户
      {
        name: 'userId',
        type: 'number',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.user` }),
        textField: 'realName',
        valueField: 'userId',
        options: userOptions,
      },
      // 权限
      {
        name: 'glAccessLevel',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.permission` }),
        textField: 'meaning',
        valueField: 'value',
        lookupCode: 'RDUCM.ACCESS_LEVEL',
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
