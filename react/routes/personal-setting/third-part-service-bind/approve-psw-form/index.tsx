import {
  Form, Password,
  DataSet, message,
} from 'choerodon-ui/pro';
import React, { useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { LabelLayoutType } from 'choerodon-ui/pro/lib/form/Form';
import ApprovePswDs from './approvePswDs';
import { siteOpenApi } from '@choerodon/master';

const ApprovePswForm = (props:any) => {
  const {
    modal, captcha, email, id,refresh
  } = props;

  const approvePswDs = useMemo(() => new DataSet(ApprovePswDs()), []);

  modal.handleOk(async () => {
    const checkRes = await approvePswDs?.validate();
    if (checkRes) {
      try {
        await siteOpenApi.createUser({
          password: approvePswDs?.current?.get('password'),
          c7nUserId: id,
          captcha,
          email,
        });
        modal.close();
        message.success('认证绑定成功');
        refresh()
      } catch (error) {
        console.log(error);
      }
    }
    return false;
  });

  return (
    <Form
      columns={2}
      labelLayout={'float' as LabelLayoutType}
      dataSet={approvePswDs}
    >
      <Password
        colSpan={2}
        name="password"
        label="密码"
      />
    </Form>
  );
};

export default observer(ApprovePswForm);
