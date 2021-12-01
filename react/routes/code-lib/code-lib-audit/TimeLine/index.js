import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { DatePicker } from 'choerodon-ui';
import { Stores, Select } from 'choerodon-ui/pro';
import PureTimeLine from './PureTimeLine.js';
import { TabKeyEnum } from '../stores';
import './log.less';

const { RangePicker } = DatePicker;
const { Option } = Select;

const TimeLine = ({
  activeProject, timeLineStore, optLogDs, activeTabKey,
}) => {
  const [isMore, setLoadMoreBtn] = useState(false);
  const [opEventTypeLookupData, setOpEventTypeLookupData] = useState([]);

  async function getOpEventTypeLookup() {
    const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUCM.OP_MM_EVENT_TYPE');
    setOpEventTypeLookupData(lookupData);
  }

  // 加载记录
  const loadData = useCallback(async (page = 1) => {
    const res = await optLogDs.query(page);
    const records = timeLineStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        optLogDs.unshift(...records);
      }
      timeLineStore.setOldOptsRecord(optLogDs.records);
      setLoadMoreBtn(res.hasNextPage);
      return res;
    }
    return false;
  }, [optLogDs]);

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.OPTLOG) {
      if (activeProject.id && activeProject.id !== 'all') {
        optLogDs.setQueryParameter('projectIds', activeProject.id.toString());
      } else if (activeProject.id === 'all') {
        optLogDs.setQueryParameter('projectIds', undefined);
      }
      loadData();
    }
  }, [activeProject, activeTabKey]);

  useEffect(() => {
    getOpEventTypeLookup();
  }, []);

  const handleSearch = (params) => {
    Object.entries(params).forEach((o) => { optLogDs.setQueryParameter(o[0], o[1]); });
    loadData();
  };

  const timeLineProps = useMemo(() => ({
    isMore, opEventTypeLookupData, loadData, optLogDs,
  }), [isMore, opEventTypeLookupData, loadData, optLogDs]);
  return (
    <div className="code-lib-audit-optlog-timeline-container">
      <div className="code-lib-audit-optlog-search">
        <RangePicker onChange={(_, dateString) => handleSearch({ startDate: dateString[0] ? `${dateString[0]} 00:00:00` : '', endDate: dateString[1] ? `${dateString[1]} 23:59:59` : '' })} />
        <Select multiple maxTagCount={1} onChange={value => handleSearch({ opEventTypes: (value || []).join(',') })} style={{ marginLeft: '0.12rem' }} placeholder="操作类型">
          {
            opEventTypeLookupData.map(o => (
              <Option key={o.value} value={o.value}>{o.meaning}</Option>
            ))
          }
        </Select>
      </div>
      <PureTimeLine {...timeLineProps} />
    </div>
  );
};

export default observer(TimeLine);
