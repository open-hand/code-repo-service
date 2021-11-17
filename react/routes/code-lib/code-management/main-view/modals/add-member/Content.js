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
  function groupAccessLevelCompare(accessLevelStr, userId) {
    // accessLevelStr 如L10
    let boolean = false;
    const accessLevelNum = accessLevelStr.substring(1);
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

  function getClusterOptionProp(record, pathRecord) {
    const userId = pathRecord.get('userId');
    if (!userId) {
      return { disabled: true };
    }
    const accessLevelNum = Number(record.data.value.substring(1));
    const { boolean } = groupAccessLevelCompare(record.data.value, userId);
    return {
      disabled: accessLevelNum >= 50 || boolean,
    };
  }

  function levelOptionsFilter(record) {
    const flag = !(Number(record.data.value.substring(1)) >= 50);
    return flag;
  }
  const AccessLevelOptionRenderer = (record, text, value, pathRecord) => {
    const userId = pathRecord.get('userId');
    const { boolean, selectedGroupAccessLevel } = groupAccessLevelCompare(
      value,
      userId,
    );
    const roleList = pathRecord.getField('glAccessLevel').options.toData();
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
  const userFilter = (record) => {
    const lev = formDs.current.get('permissionsLevel');
    const exist = some(
      pathListDs.created,
      r => r.get('userId') === record.get('userId'),
    );
    if (exist) {
      // 前面已经选过了
      return false;
    }
    if (lev === 'applicationService') {
      return true;
    }
    if (record.get('groupAccessLevel')) {
      // 已经有全局权限了
      return false;
    }
    return true;
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
      // 项目内部成员
      return !exist && nameMatching;
    }
    return !exist;
  }
  const queryUser = debounce(async (str) => {
    userOptions.setQueryParameter('name', str);
    userOptions.setQueryParameter('type', openType);
    if (str !== '') {
      const existArr = []; // 公用的一个userOptions。防止重新获取后之前的value匹配不到
      pathListDs.created.forEach((record) => {
        if (record.get('userId')) {
          let loginName;
          userOptions.forEach((userRecord) => {
            if (userRecord.get('userId') === record.get('userId')) {
              loginName = userRecord.get('loginName');
            }
          });
          existArr.push({
            userId: record.getField('userId').getValue(),
            realName: record.getField('userId').getText(),
            loginName,
          });
        }
      });
      await userOptions.query();
      userOptions.appendData(existArr);
    }
  }, 500);

  const handleUserSearch = (e) => {
    e.persist();
    if (openType === 'project') {
      return;
    }
    queryUser(e.target.value);
  };
  const optionRenderer = ({ text, textField, record }) => (
    <Tooltip title={record.get('repositoryCode')} placement="left">
      <span style={{ width: '100%' }}>
        {`${text}(${record.get('repositoryCode')})`}
      </span>
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
            maxTagTextLength={16}
            searchMatcher={({ record, text, textField }) =>
              record.get('repositoryCode').indexOf(text) !== -1 ||
              record.get(textField).indexOf(text) !== -1
            }
            optionRenderer={optionRenderer}
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
            optionsFilter={userFilter}
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
            onOption={({ record }) => getClusterOptionProp(record, pathRecord)}
            optionsFilter={levelOptionsFilter}
            optionRenderer={({ record, text, value }) =>
              AccessLevelOptionRenderer(record, text, value, pathRecord)
            }
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
