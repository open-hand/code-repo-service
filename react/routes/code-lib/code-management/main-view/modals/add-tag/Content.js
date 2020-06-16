import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import { useAddTagStore } from './stores';

export default observer(() => {
  const {
    tagFormDs,
    modal,
    refresh,
  } = useAddTagStore();
  modal.handleOk(async () => {
    try {
      if (await tagFormDs.submit() !== false) {
        refresh();
        return true;
      } else {
        return false;
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  });
  
  modal.handleCancel(() => {
    tagFormDs.reset();
  });

  function handleChange(value) {
    tagFormDs.current.set('tagName', value);
  }

  return (
    <div>
      <Form dataSet={tagFormDs}>
        <Select name="tagName" combo onChange={handleChange} clearButton={false} />
        <Select name="createAccessLevel" clearButton={false} />
      </Form>
    </div>
  );
});
