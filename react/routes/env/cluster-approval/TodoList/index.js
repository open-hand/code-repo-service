import React, { useEffect, useCallback } from 'react';
import { Tooltip, Tag } from 'choerodon-ui';
import { Table, Modal } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import { TabKeyEnum } from '../ClusterApproval';
import ApproveForm from './ApproveForm';

const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
  flexShrink: 0,
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104, 135, 232, 1)',
  borderRadius: '50%',
  lineHeight: '18px',
  textAlign: 'center',
  flexShrink: 0,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
};

const { Column } = Table;
const TodoList = ({ todoListDs, formatMessage, activeTabKey }) => {
  useEffect(() => {
    if (activeTabKey === TabKeyEnum.TODO) {
      todoListDs.query();
    }
  }, [activeTabKey]);

  const rendererIcon = useCallback((imageUrl, text) => {
    let iconElement;
    if (imageUrl) {
      iconElement = <img src={imageUrl} alt="" style={imgStyle} />;
    } else {
      iconElement = <div style={iconStyle}>{text[0]}</div>;
    }
    return (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {iconElement}
        <span style={{ marginLeft: '7px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {text}
        </span>
      </div>
    );
  }, []);


  const handleOpenEditModal = useCallback(async (data, record) => {
    if (data.state !== 'approving') {
      const key = Modal.key();
      Modal.open({
        key,
        title: formatMessage({ id: 'infra.env.cluster.view.detail.title', defaultMessage: `集群申请单：${data.applicationNo}` }, { applicationNo: data.applicationNo }),
        maskClosable: true,
        destroyOnClose: true,
        drawer: true,
        className: 'env-application-model',
        children: <ApproveForm data={data} todoListDs={todoListDs} formatMessage={formatMessage} record={record} />,
      });
    }
  }, [todoListDs]);


  const rendererDropDown = ({ text, record }) =>
    (
      <span className={record.get('state') !== 'approving' && 'link-cell'} onClick={() => handleOpenEditModal(record.toData(), record)}>
        <Tooltip title={text}>
          {text}
        </Tooltip>
      </span>
    );

  const getTagColor = (value) => {
    switch (value) {
      case 'waiting_approve':
        return 'rgba(255, 177, 0, 1)';
      case 'approved':
        return 'rgba(0, 191, 165, 1)';
      case 'rejected':
        return 'rgba(244, 67, 54, 1)';
      case 'approving':
        return 'rgb(0, 191, 165)';
      default:
        break;
    }
  };

  return (
    <Table dataSet={todoListDs} queryBar="none" className="no-border-top-table" >
      <Column name="applicationNo" renderer={rendererDropDown} />
      <Column name="state" renderer={({ text, value }) => <Tag className="cluster-approval-list-state-tag" color={getTagColor(value)}>{text}</Tag>} />
      <Column name="applicationPurpose" />
      <Column name="projectName" />
      <Column name="user" renderer={({ record }) => rendererIcon(record.toData().user.imageUrl, record.toData().user.realName)} />
      <Column name="applicationDate" renderer={({ value }) => value && <Timeago date={value} />} />
    </Table>
  );
};

export default observer(TodoList);
