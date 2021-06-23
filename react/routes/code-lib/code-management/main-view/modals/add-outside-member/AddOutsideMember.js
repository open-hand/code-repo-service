import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, Tooltip, Icon, DatePicker } from 'choerodon-ui/pro';
import { debounce } from 'lodash';
import moment from 'moment';
import { Choerodon } from '@choerodon/boot';
import TwoFormSelectEditor from '@/components/twoFormSelectEditor';

import UserOptionDataSet from './stores/UserNoDataSet';
import { useAddMemberStore } from './stores';
import './index.less';

export default observer((props) => {
  const {
    prefixCls,
    intl: { formatMessage },
    organizationId,
    projectId,
    formDs,
    pathListDs,
    modal,
    refresh,
    dsStore,
    glAccessLevelDataSet,
    branchServiceDs,
  } = useAddMemberStore();

  useEffect(()=>{
    console.log(branchServiceDs);
    console.log(branchServiceDs.current);
  }, [branchServiceDs]);

  function handleCancel() {
    pathListDs.reset();
  }

  modal.handleOk(async () => {
    try {
      formDs.members = pathListDs;
      if (await formDs.submit()) {
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

  modal.handleCancel(handleCancel);

  const queryUser = debounce((str, optionDataSet) => {
    optionDataSet.setQueryParameter('name', str);
    if (str !== '') { optionDataSet.query(); }
  }, 500);
  function handleFilterChange(e, optionDataSet) {
    e.persist();
    queryUser(e.target.value, optionDataSet);
  }

  function getOption({ record }) {
    return (
      <Tooltip placement="left" title={`${record.get('email')}`}>
        <div className={`${prefixCls}-option`}>
          <div className={`${prefixCls}-option-avatar`}>
            {
              record.get('imageUrl') ? <img src={record.get('imageUrl')} alt="userAvatar" style={{ width: '100%' }} />
                : <span className={`${prefixCls}-option-avatar-noavatar`}>{record.get('realName') && record.get('realName').split('')[0]}</span>
            }
          </div>
          <span>{record.get('realName')}</span>
          {record.get('ldap') && record.get('loginName') ? (
            <span>({record.get('loginName')})</span>
          ) : null}
        </div>
      </Tooltip>

    );
  }

  function getClusterOptionProp({ record }) {
    return {
      disabled: Number(record.data.value.substring(1)) >= 50,
    };
  }
  function levelOptionsFilter(record) {
    const flag = !(Number(record.data.value.substring(1)) >= 50);
    return flag;
  }

  return (
    <div
      style={{ width: '5.12rem' }}
    >
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
      <TwoFormSelectEditor
        formDs={formDs}
        record={[pathListDs.current, pathListDs.current, pathListDs.current]}
        optionDataSetConfig={[UserOptionDataSet({ organizationId, projectId }), undefined]}
        optionDataSet={[undefined, glAccessLevelDataSet]}
        name={['userId', 'glAccessLevel', 'glExpiresAt']}
        addButton={formatMessage({ id: 'infra.add.outsideMember' })}
        dsStore={[dsStore]}
      >
        {[(itemProps) => (
          <Select
            {...itemProps}
            colSpan={4}
            labelLayout="float"
            searchable
            searchMatcher={() => true}
            onInput={(e) => handleFilterChange(e, itemProps.options)}
            style={{ width: '100%' }}
            optionRenderer={getOption}
            addonAfter={(
              <Tooltip title={formatMessage({ id: 'infra.add.outsideMember.tips' })}>
                <Icon type="help" className={`${prefixCls}-help-icon`} />
              </Tooltip>
            )}
          />
        ), (itemProps) => (
          <Select
            {...itemProps}
            labelLayout="float"
            style={{ width: '100%' }}
            onOption={getClusterOptionProp}
            optionsFilter={levelOptionsFilter}
          />
        ), (itemProps) => (
          <DatePicker
            {...itemProps}
            labelLayout="float"
            style={{ width: '100%' }}
            min={moment().add(1, 'days').format('YYYY-MM-DD')}
            colSpan={4}
          />
        )]}
      </TwoFormSelectEditor>
    </div>
  );
});
