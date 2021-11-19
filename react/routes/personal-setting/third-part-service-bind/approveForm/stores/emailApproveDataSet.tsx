/* eslint-disable import/no-anonymous-default-export */

export default (():any => ({
  autoCreate: true,
  fields: [
    {
      name: 'email',
      type: 'email',
      label: '邮箱',
      required: true,
    },
    {
      label: '验证码',
      required: true,
      name: 'captcha',
      maxLength: 6,
    },
    {
      label: 'user_id',
      name: 'user_id',
    },
  ],
}));
