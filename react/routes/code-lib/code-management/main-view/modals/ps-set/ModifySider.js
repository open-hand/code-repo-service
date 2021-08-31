import React from 'react';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import { Form, Select, DatePicker, TextField } from 'choerodon-ui/pro';

export default observer((props) => {
  const { modal, psSetDs } = props;
  async function handleOk() {
    if (psSetDs.current && !psSetDs.current.dirty && !psSetDs.current.get('dirty')) {
      return true;
    }
    if (await psSetDs.submit()) {
      psSetDs.query();
      return true;
    }
    return false;
  }

  function getClusterOptionProp({ record }) {
    const numValue = Number(record.data.value.substring(1));
    if (numValue >= 50) {
      return {
        disabled: true,
      };
    }
    //  打开的是应用服务,并且这个人有全局权限
    if (psSetDs.current.get('type') === 'project' && psSetDs.current.get('groupAccessLevel')) {
      return {
        disabled: psSetDs.current.get('groupAccessLevel') >= numValue,
      };
    }
    return {
      disabled: false,
    };
  }

  function renderRole({ value }) {
    return value.join();
  }

  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    psSetDs.reset();
  });

  return (
    <div>
      <Form dataSet={psSetDs}>
        <TextField name="realName" disabled />
        <TextField name="loginName" disabled />
        <TextField name="repositoryName" disabled />
        <TextField name="roleNames" disabled renderer={renderRole} />
        <Select name="glAccessLevel" onOption={getClusterOptionProp} />
        <DatePicker name="glExpiresAt" min={moment().add(1, 'days').format('YYYY-MM-DD')} />
      </Form>
    </div>
  );
});
