import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, Button, DatePicker } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import { map, some } from 'lodash';
import moment from 'moment';
import { useAddMemberStore } from './stores';
import './index.less';

export default observer(() => {
  const {
    formDs,
    pathListDs,
    intl: { formatMessage },
    modal,
    refresh,
  } = useAddMemberStore();

  modal.handleOk(async () => {
    try {
      if (await formDs.submit() !== false) {
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

  function handleAddPath() {
    pathListDs.create();
  }

  function handleRemovePath(removeRecord) {
    pathListDs.remove(removeRecord);
  }

  function getClusterOptionProp({ record }) {
    return {
      disabled: Number(record.data.value.substring(1)) >= 50,
    };
  }

  function optionsFilter(record) {
    const flag = some(pathListDs.created, (r) => r.get('userId') === record.get('userId'));
    return !flag;
  }
  function levelOptionsFilter(record) {
    const flag = !(Number(record.data.value.substring(1)) >= 50);
    return flag;
  }
  function searchMatcher({ record, text, textField }) {
    const isTrue = record.get(textField).indexOf(text) !== -1 || record.get('loginName').indexOf(text) !== -1;
    return isTrue;
  }

  return (
    <div style={{ width: '5.12rem' }}>
      <Form dataSet={formDs} columns={6}>
        <Select
          multiple
          name="repositoryIds"
          searchable
          maxTagCount={3}
          maxTagTextLength={6}
          maxTagPlaceholder={restValues => `+${restValues.length}...`}
          dropdownMenuStyle={{ width: '5.12rem' }}
          colSpan={6}
        />
      </Form>
      {map(pathListDs.data, (pathRecord) => (
        <Form record={pathRecord} columns={13} key={pathRecord.id} className="code-lib-management-add-member">
          <Select
            name="userId"
            searchable
            colSpan={4}
            optionsFilter={optionsFilter}
            searchMatcher={searchMatcher}
          />
          <Select
            name="glAccessLevel"
            colSpan={4}
            onOption={getClusterOptionProp}
            optionsFilter={levelOptionsFilter}
          />
          <DatePicker popupCls="code-lib-management-add-member-dayPicker" name="glExpiresAt" min={moment().add(1, 'days').format('YYYY-MM-DD')} colSpan={4} />
          {pathListDs.length > 1 ? (
            <Button
              funcType="flat"
              icon="delete"
              style={{
                marginTop: '8px'
              }}
              onClick={() => handleRemovePath(pathRecord)}
            />
          ) : <span />}
        </Form>
      ))}
      <Button
        funcType="flat"
        color="primary"
        icon="add"
        onClick={handleAddPath}
      >
        {formatMessage({ id: 'infra.add.member' })}
      </Button>
    </div>
  );
});
