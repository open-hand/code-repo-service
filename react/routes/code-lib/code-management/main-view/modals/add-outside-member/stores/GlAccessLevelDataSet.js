export default () => ({
  autoQuery: true,
  selection: 'single',
  paging: false,

  transport: {
    read: {
      url: '/hpfm/v1/lovs/value?lovCode=RDUCM.ACCESS_LEVEL',
      method: 'get',
      dataKey: null,
    },
  },
  fields: [
    { name: 'meaning', type: 'string' },
    { name: 'value', type: 'string' },
  ],
});
