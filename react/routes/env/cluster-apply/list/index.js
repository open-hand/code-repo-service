import React from 'react';
import { observer } from 'mobx-react-lite';
import { Button } from 'choerodon-ui';
import { Spin } from 'choerodon-ui/pro';
import EmptyPage from '@/components/empty';
import Card from './Card';

const ListView = observer((props) => {
  const { applyListDs, formatMessage, intlPrefix, isMore } = props;

  function renderCard(record) {
    const cardPlainObj = record.toData();
    return (
      <Card
        {...cardPlainObj}
        {...props}
        dataSet={applyListDs}
        record={record}
        formatMessage={formatMessage}
        intlPrefix={intlPrefix}
      />
    );
  }

  // const listData = applyListDs.current && applyListDs.toData();

  if (applyListDs.length === 0) {
    return (
      <EmptyPage
        loading={applyListDs.status === 'loading'}
        title={formatMessage({ id: `${intlPrefix}.view.nodata.title`, defaultMessage: '暂无申请单' })}
        description={formatMessage({ id: `${intlPrefix}.view.nodata.content`, defaultMessage: '暂无可操作的集群申请单' })}
      />);
  }

  // 更多操作
  function loadMoreOptsRecord() {
    props.loadData(applyListDs.currentPage + 1);
  }

  return (
    <Spin dataSet={applyListDs} >
      <div style={{ display: 'flex', flexDirection: 'row', flexWrap: 'wrap' }}>
        {applyListDs.map(r => renderCard(r))}
      </div>
      {isMore && <Button type="primary" onClick={loadMoreOptsRecord}>{formatMessage({ id: 'infra.codelib.audit.view.loadMore' })}</Button>}
    </Spin>
  );
});

export default ListView;
