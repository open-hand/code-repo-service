import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select } from 'choerodon-ui/pro';

export default observer((props) => {
  const { modal, tagFormDs, onOk } = props;
  async function handleOk() {
    if (tagFormDs.current && !tagFormDs.current.dirty && !tagFormDs.current.get('dirty')) {
      return true;
    }
    if (await tagFormDs.submit()) {
      onOk();
      tagFormDs.reset();
      return true;
    } else {
      return false;
    }
  }
  
  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    tagFormDs.reset();
  });

  return (
    <div>
      <Form dataSet={tagFormDs}>
        <Select name="name" disabled clearButton={false} />
        <Select name="createAccessLevel" clearButton={false} />
      </Form>
    </div>
  );
});
