import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage, Header, Page, Content, HeaderButtons } from '@choerodon/boot';
import { useFormatMessage } from '@choerodon/master';
import { Button, DatePicker, Tooltip } from 'choerodon-ui';
import { Stores, Select } from 'choerodon-ui/pro';

import CodeManagerHeader from '../../header';
import TimeLine from './TimeLine.js';
import { usPsManagerStore } from '../stores';
import './log.less';


const { RangePicker } = DatePicker;
const { Option } = Select;

const OperationLogTab = () => {
  const {
    listViewDs,
    overStores,
    branchServiceDs,
  } = usPsManagerStore();

  const format = useFormatMessage('c7ncd.codeLibManagement');

  const [isMore, setLoadMoreBtn] = useState(false);
  const [opEventTypeLookupData, setOpEventTypeLookupData] = useState([]);

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    const res = await listViewDs.query(page);
    const records = overStores.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        listViewDs.unshift(...records);
      }
      overStores.setOldOptsRecord(listViewDs.records);
      setLoadMoreBtn(res.hasNextPage);
      return res;
    }
    return false;
  }, [listViewDs]);

  async function getOpEventTypeLookup() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUCM.OP_MM_EVENT_TYPE');
    setOpEventTypeLookupData(lookupData);
  }

  const init = () => {
    loadData();
    getOpEventTypeLookup();
  };

  useEffect(() => {
    init();
  }, []);

  const handleSearch = (params) => {
    Object.entries(params).forEach((o) => { listViewDs.setQueryParameter(o[0], o[1]); });
    loadData();
  };

  const timeLineProps = useMemo(() => ({
    isMore, opEventTypeLookupData, loadData, listViewDs,
  }), [isMore, opEventTypeLookupData, loadData, listViewDs]);

  const renderOption = ({ record }) => {
    const externalConfigId = record?.get('externalConfigId');
    const repositoryName = record?.get('repositoryName');
    return (
      <Tooltip
        title={externalConfigId ? '外置GitLab代码仓库的应用服务不支持配置' : ''}
      >
        {repositoryName}
      </Tooltip>
    );
  };

  const onOption = ({ record }) => {
    const externalConfigId = record?.get('externalConfigId');
    return ({
      disabled: Boolean(externalConfigId),
    });
  };

  const optionsFilter = (record) => {
    let flag = true;
    if (record?.get('repositoryId') === 'all') {
      flag = false;
    }
    return flag;
  };


  return (
    <div style={{
      height: '100%',
    }}
    >
      <Header>
        <HeaderButtons
          showClassName={false}
          items={([{
            color: 'default',
            icon: 'refresh',
            iconOnly: true,
            handler: () => init(),
          }])}
        />
      </Header>
      <CodeManagerHeader />
      <Content style={{
        height: 'calc(100% - 64px)',
        overflow: 'scroll',
      }}
      >
        <div className="code-lib-opreation-log-search">
          <Select
            name="repositoryIds"
            placeholder={format({ id: 'ApplicationService' })}
            onChange={val => handleSearch({ repositoryIds: val.repositoryId })}
              // eslint-disable-next-line
            clearButton={false}
            dataSet={branchServiceDs}
            searchable
            style={{ maxWidth: '2.85rem', marginRight: '0.12rem' }}
            optionRenderer={renderOption}
            onOption={onOption}
            optionsFilter={optionsFilter}
          >
            {/* {
                branchServiceDs.toData().map(o => (
                  <Option
                    key={o.repositoryId}
                    value={o.repositoryId}
                    disabled={Boolean(o.externalConfigId)}
                  >
                    <Tooltip title={o.externalConfigId ? '外置GitLab代码仓库的应用服务不支持配置' : ''}>
                      {o.repositoryName}
                    </Tooltip>
                  </Option>
                ))
              } */}
          </Select>
          <RangePicker onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : '', endDate: dateString[1] ? `${dateString[1]} 23:59:59` : '' })} />
          <Select placeholder={format({ id: 'OperationType' })} onChange={value => handleSearch({ opEventTypes: value })} style={{ marginLeft: '0.12rem' }}>
            {
                opEventTypeLookupData.map(o => (
                  <Option key={o.value} value={o.value}>{o.meaning}</Option>
                ))
              }
          </Select>
        </div>
        <TimeLine {...timeLineProps} />
      </Content>
      {/* </Page> */}
    </div>
  );
};

export default observer(OperationLogTab);
