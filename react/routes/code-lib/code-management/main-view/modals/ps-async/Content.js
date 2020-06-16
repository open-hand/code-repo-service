import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';
import { Form, SelectBox } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';

const { Option } = SelectBox;

export default observer((props) => {
  const { modal, formatMessage, organizationId, projectId, dataSet, record, onOk, onAsyncPermission } = props;

  const [syncStrategy, setSyncStrategy] = useState(1);

  const handleChange = (value) => {
    setSyncStrategy(value);
  };

  async function handleOk() {
    const params = {
      organizationId,
      projectId,
      id: record.get('id'),
      syncStrategy,
    };
    const result = await onAsyncPermission(params).then((res) => {
      if (res.failed) {
        message.error(res.message);
        return false;
      } else {
        onOk();
        message.success(formatMessage({ id: 'infra.codeManage.ps.message.asyncSuccess' }));
        dataSet.reset();
        return true;
      }
    })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        return false;
      });
    return result;
  }
  
  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    dataSet.reset();
  });

  return (
    <div>
      <Form labelLayout="vertical">
        <SelectBox label={formatMessage({ id: 'infra.codeManage.ps.model.syncStrategy' })} name="syncStrategy" value={syncStrategy} onChange={handleChange}>
          <Option value={1}>{formatMessage({ id: 'infra.codeManage.ps.model.currentToGitlab' })}</Option>
          <Option value={2}>{formatMessage({ id: 'infra.codeManage.ps.model.gitlabToCurrent' })}</Option>
        </SelectBox>
      </Form>
    </div>
  );
});
