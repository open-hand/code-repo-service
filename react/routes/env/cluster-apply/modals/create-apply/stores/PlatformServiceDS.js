export default ({ formatMessage, intlPrefix }) => (
  {
    autoCreate: false,
    autoQuery: false,
    selection: false,
    paging: false,
    fields: [
      {
        name: 'name',
        type: 'string',
        required: true,
        label: formatMessage({ id: 'name' }),
      },
      {
        name: 'desc',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.model.detailInfo` }),
      },
      {
        name: 'version',
        type: 'string',
        label: formatMessage({ id: 'version' }),
      },
      {
        name: 'configuration',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.configuration` }),
      },
    ],
  }
);
