import React, { useEffect, useCallback } from 'react';
import { Tooltip, Tag } from 'choerodon-ui';
import { Table, Modal } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import { TabKeyEnum } from '../ClusterApproval';
import ApplicationForm from './ApplicationForm';


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
const DoneList = ({ doneListDs, formatMessage, activeTabKey }) => {
  useEffect(() => {
    if (activeTabKey === TabKeyEnum.DONE) {
      doneListDs.query();
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
        <span
          style={{
            marginLeft: '7px',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          {text}
        </span>
      </div>
    );
  }, []);


  const handleOpenEditModal = useCallback(async (data) => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: 'infra.env.cluster.view.detail.title', defaultMessage: `集群申请单：${data.applicationNo}` }, { applicationNo: data.applicationNo }),
      maskClosable: true,
      destroyOnClose: true,
      drawer: true,
      okCancel: false,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
      className: 'env-application-model',
      children: <ApplicationForm data={data} />,
    });
  }, []);


  const rendererDropDown = ({ text, record }) =>
    (
      <span className="link-cell" onClick={() => handleOpenEditModal(record.toData())}>
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
      default:
        break;
    }
  };

  return (
    <Table dataSet={doneListDs} queryBar="none" className="no-border-top-table" >
      <Column name="applicationNo" renderer={rendererDropDown} />
      <Column name="state" renderer={({ text, value }) => <Tag className="cluster-approval-list-state-tag" color={getTagColor(value)}>{text}</Tag>} />
      <Column name="applicationPurpose" />
      <Column name="projectName" />
      <Column name="user" renderer={({ record }) => record.toData().user && rendererIcon(record.toData().user.imageUrl, record.toData().user.realName)} />
      <Column name="applicationDate" renderer={({ value }) => value && <Timeago date={value} />} />
    </Table>
  );
};

export default observer(DoneList);
