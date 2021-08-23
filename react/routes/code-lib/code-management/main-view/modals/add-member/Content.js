import { axios, Choerodon } from '@choerodon/boot';
import React from 'react';
import { observer } from 'mobx-react-lite';
import {
  Form,
  Select,
  Button,
  DatePicker,
  Tooltip,
  SelectBox,
} from 'choerodon-ui/pro';

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
    userOptions,
  } = useAddMemberStore();

  modal.handleOk(async () => {
    try {
      if ((await formDs.submit()) !== false) {
        refresh();
        return true;
      }
      return false;
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
    const levelNum = Number(record.data.value.substring(1));
    let boolean = false;
    const userId = pathListDs.current.get('userId');
    let groupAccessLevel; // 当前选中项的全局权限值
    userOptions.toData().forEach((item) => {
      if (userId === item.userId) {
        // eslint-disable-next-line prefer-destructuring
        groupAccessLevel = item.groupAccessLevel;
      }
    });
    // 有层级权限并且当前是应用服务授予权限
    if (
      groupAccessLevel &&
          formDs.current.get('permissionsLevel') === 'applicationService' &&
          levelNum <= groupAccessLevel
    ) {
      boolean = true;
    }
    return {
      disabled: levelNum >= 50 || boolean,
    };
  }

  function optionsFilter(record) {
    const flag = some(
      pathListDs.created,
      r => r.get('userId') === record.get('userId'),
    );
    return !flag;
  }
  function levelOptionsFilter(record) {
    const flag = !(Number(record.data.value.substring(1)) >= 50);
    return flag;
  }
  const AccessLevelOptionRenderer = ({ record, text, value }) => {
    const userId = pathListDs.current.get('userId');
    let groupAccessLevel; // 当前选中项的全局权限值
    userOptions.toData().forEach((item) => {
      if (userId === item.userId) {
        // eslint-disable-next-line prefer-destructuring
        groupAccessLevel = item.groupAccessLevel;
      }
    });
    const roleList = pathListDs.current
      .getField('glAccessLevel')
      .options.toData();
    let str = text;
    // 有层级权限并且当前是应用服务授予权限
    if (
      groupAccessLevel &&
      formDs.current.get('permissionsLevel') === 'applicationService' &&
      Number(value.substring(1)) <= groupAccessLevel
    ) {
      const levelStr = `L${groupAccessLevel}`;
      roleList.forEach((item) => {
        if (item.value === levelStr) {
          str = `该用户已被分配项目全局的${item.meaning}权限`;
        }
      });
    }
    return (
      <Tooltip title={str} placement="left">
        <div>{`${text}`}</div>
      </Tooltip>
    );
  };
  function searchMatcher({ record, text, textField }) {
    const isTrue =
      record.get(textField).indexOf(text) !== -1 ||
      record.get('loginName').indexOf(text) !== -1;
    return isTrue;
  }
  const renderer = ({ text, textField, record }) => (
    <span style={{ width: '100%' }}>
      {text}({record.get('repositoryCode')})
    </span>
  );
  const optionRenderer = ({ text, textField, record }) => (
    <Tooltip title={record.get('repositoryCode')} placement="left">
      {renderer({ text, record })}
    </Tooltip>
  );
  // console.log(formDs?.current?.get('permissionsLevel'));
  return (
    <div style={{ width: '5.12rem' }}>
      <Form dataSet={formDs} columns={1}>
        <SelectBox name="permissionsLevel" />
        {formDs?.current?.get('permissionsLevel') === 'applicationService' && (
          <Select
            multiple
            name="repositoryIds"
            searchable
            maxTagCount={3}
            maxTagTextLength={6}
            searchMatcher={({ record, text, textField }) =>
              record.get('repositoryCode').indexOf(text) !== -1 ||
              record.get(textField).indexOf(text) !== -1
            }
            optionRenderer={optionRenderer}
            renderer={renderer}
            maxTagPlaceholder={restValues => `+${restValues.length}...`}
            dropdownMenuStyle={{ width: '5.12rem' }}
            colSpan={6}
          />
        )}
      </Form>
      {map(pathListDs.data, (pathRecord) => {
        console.log(pathRecord.get, 'pathRecord');
        return (
          <Form
            record={pathRecord}
            columns={13}
            key={pathRecord.id}
            className="code-lib-management-add-member"
          >
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
              optionRenderer={AccessLevelOptionRenderer}
            />
            <DatePicker
              popupCls="code-lib-management-add-member-dayPicker"
              name="glExpiresAt"
              min={moment()
                .add(1, 'days')
                .format('YYYY-MM-DD')}
              colSpan={4}
            />
            {pathListDs.length > 1 ? (
              <Button
                funcType="flat"
                icon="delete"
                style={{
                  marginTop: '8px',
                }}
                onClick={() => handleRemovePath(pathRecord)}
              />
            ) : (
              <span />
            )}
          </Form>
        );
      })}
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
