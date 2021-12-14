/* eslint-disable import/no-anonymous-default-export */
export default ({
  formatMessage, intlPrefix, userOptionsPermission,
}) => (
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
        // required: true,
        label: formatMessage({ id: `${intlPrefix}.user` }),
        textField: 'realName',
        valueField: 'userId',
        options: userOptionsPermission,
      },
    ],
  }
);

