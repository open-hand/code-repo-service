export default ({ formatMessage, intlPrefix, infrastructureDs, platformServiceDs }) => (
  {
    autoCreate: false,
    autoQuery: false,
    selection: false,
    paging: false,
    children: {
      infrastructure: infrastructureDs,
      platformService: platformServiceDs,
    },
    fields: [
      {
        name: 'others',
        type: 'string',
      },
      {
        name: 'desc',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.detailInfo` }),
        bind: 'others.desc',
        ignore: 'always',
      },
    ],
  }
);
