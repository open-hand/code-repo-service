import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage } from '@choerodon/boot';
import { Button, DatePicker } from 'choerodon-ui';
import { Stores, Select } from 'choerodon-ui/pro';
import { Header, Page, Content } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
import TimeLine from './TimeLine.js';
import { usPsManagerStore } from '../stores';
import './log.less';

const { RangePicker } = DatePicker;
const { Option } = Select;

const OperationLogTab = () => {
  const {
    intl: { formatMessage },
    listViewDs,
    overStores,
    branchServiceDs,
  } = usPsManagerStore();

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
    } else {
      return false;
    }
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
    Object.entries(params).forEach(o => { listViewDs.setQueryParameter(o[0], o[1]); });
    loadData();
  };

  const timeLineProps = useMemo(() => ({ isMore, opEventTypeLookupData, loadData, listViewDs }), [isMore, opEventTypeLookupData, loadData, listViewDs]);
  return (
    <TabPage>
      <Header>
        <Button onClick={() => init()} icon="refresh">
          {formatMessage({ id: 'refresh' })}
        </Button>
      </Header>
      <CodeManagerHeader />
      {/* <div style={{ paddingLeft: 24, display: 'flex', alignItems: 'center' }}>
        <Form columns={2} style={{ maxWidth: '3.5rem' }} >
          <Select
            onChange={val => handleSearch({ repositoryId: val })}
            // eslint-disable-next-line
            clearButton={true}
            style={{ maxWidth: '3.5rem' }}
          >
            {
              branchServiceDs.toData().map(o => (
                <Option key={o.repositoryId} value={o.repositoryId}>{o.repositoryName}</Option>
              ))
            }
          </Select>
        </Form>
      </div> */}
      <Page
        service={[
          'choerodon.code.project.infra.code-lib-management.ps.project-owner',
        ]}
        className="code-lib-opreation-log-page"
      >
        <Content>
          <div className="code-lib-opreation-log-search">
            <Select
              placeholder={formatMessage({ id: 'infra.codelib.audit.model.service' })}
              onChange={val => handleSearch({ repositoryIds: val })}
              // eslint-disable-next-line
              clearButton={true}
              style={{ maxWidth: '2.85rem', marginRight: '0.12rem' }}
            >
              {
                branchServiceDs.toData().map(o => (
                  <Option key={o.repositoryId} value={o.repositoryId}>{o.repositoryName}</Option>
                ))
              }
            </Select>
            <RangePicker onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : '', endDate: dateString[1] ? `${dateString[1]} 23:59:59` : '' })} />
            <Select placeholder={formatMessage({ id: 'infra.codelib.audit.model.opType' })} onChange={(value) => handleSearch({ opEventTypes: value })} style={{ marginLeft: '0.12rem' }}>
              {
                opEventTypeLookupData.map(o => (
                  <Option key={o.value} value={o.value}>{o.meaning}</Option>
                ))
              }
            </Select>
          </div>
          <TimeLine {...timeLineProps} />
        </Content>
      </Page>
    </TabPage>
  );
};

export default observer(OperationLogTab);
