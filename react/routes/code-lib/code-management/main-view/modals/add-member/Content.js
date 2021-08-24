import { Choerodon } from '@choerodon/boot';
import React from 'react';
import { observer } from 'mobx-react-lite';
import {
  Form,
  Select,
  Button,
  DatePicker,
  Tooltip,
  SelectBox,
  Icon,
} from 'choerodon-ui/pro';
import { map, some, debounce } from 'lodash';
import moment from 'moment';
import { useAddMemberStore } from './stores';
import './index.less';

export default observer(() => {
  const {
    openType,
    prefixCls,
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

  // 应用服务时,选中人如果有权限等级 拿它和accessLevelStr的权限进行比较
  function groupAccessLevelCompare(accessLevelStr) {
    // accessLevelStr 如L10
    let boolean = false;
    const accessLevelNum = accessLevelStr.substring(1);
    const userId = pathListDs.current.get('userId');
    let selectedGroupAccessLevel; // 当前选中项的全局权限值
    userOptions.toData().forEach((item) => {
      if (userId === item.userId) {
        selectedGroupAccessLevel = item.groupAccessLevel;
      }
    });
    // 有层级权限并且当前是应用服务授予权限
    if (
      selectedGroupAccessLevel &&
      formDs.current.get('permissionsLevel') === 'applicationService' &&
      accessLevelNum <= selectedGroupAccessLevel
    ) {
      boolean = true;
    }
    return {
      selectedGroupAccessLevel,
      boolean,
    };
  }

  function getClusterOptionProp({ record }) {
    const accessLevelNum = Number(record.data.value.substring(1));
    const { boolean } = groupAccessLevelCompare(record.data.value);
    return {
      disabled: accessLevelNum >= 50 || boolean,
    };
  }

  function levelOptionsFilter(record) {
    const flag = !(Number(record.data.value.substring(1)) >= 50);
    return flag;
  }
  const AccessLevelOptionRenderer = ({ record, text, value }) => {
    const { boolean, selectedGroupAccessLevel } = groupAccessLevelCompare(value);
    const roleList = pathListDs.current
      .getField('glAccessLevel')
      .options.toData();
    let str = text;
    // 有层级权限并且当前是应用服务授予权限
    if (boolean) {
      const accessLevelStr = `L${selectedGroupAccessLevel}`;
      roleList.forEach((item) => {
        if (item.value === accessLevelStr) {
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
    const exist = some(
      pathListDs.created,
      r => r.get('userId') === record.get('userId'),
    );
    const nameMatching =
      record.get(textField).indexOf(text) !== -1 ||
      record.get('loginName').indexOf(text) !== -1;
    if (openType === 'project') {
      return !exist && nameMatching;
    }
    return !exist;
  }
  const queryUser = debounce((str) => {
    userOptions.setQueryParameter('name', str);
    userOptions.setQueryParameter('type', openType);
    if (str !== '') {
      userOptions.query();
    }
  }, 500);

  const handleUserSearch = (e) => {
    e.persist();
    if (openType === 'project') {
      return;
    }
    queryUser(e.target.value);
  };
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
      {map(pathListDs.data, pathRecord => (
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
            searchMatcher={({ record, text, textField }) =>
              searchMatcher({ record, text, textField })
            }
            onInput={(e) => {
              handleUserSearch(e);
            }}
            addonAfter={
              openType === 'project' ? null : (
                <Tooltip
                  title={formatMessage({ id: 'infra.add.outsideMember.tips' })}
                >
                  <Icon type="help" className={`${prefixCls}-user-help-icon`} />
                </Tooltip>
              )
            }
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
      ))}
      <Button
        funcType="flat"
        color="primary"
        icon="add"
        onClick={handleAddPath}
      >
        {formatMessage({
          id:
            openType === 'project'
              ? 'infra.add.member'
              : 'infra.add.outsideMember',
        })}
      </Button>
    </div>
  );
});
