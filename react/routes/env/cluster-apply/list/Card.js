import React from 'react';
import { Tooltip, Modal } from 'choerodon-ui/pro';
import { Icon, Row, Col } from 'choerodon-ui';
import { Action } from '@choerodon/boot';
import { isNil } from 'lodash';
import UserAvatar from '@/components/user-avatar';
import TimePopover from '@/components/time-popover/TimePopover';

import './card.less';

const Card = ({ onDelete, onUpdate, onViewDetail, dataSet, record, isMore, loadData, formatMessage, intlPrefix, ...props }) => {
  const { id, applicationNo, state, user, applicationDate, applicationPurpose, currentActivity, getPurposeName } = props;

  function renderTime(value) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }
  // const getApplicationNo = () => `...${applicationNo.substring(applicationNo.length - 16)}`;
  const getStatusMeaning = () => {
    let meaning = '';
    let style;
    switch (state) {
      case 'waiting_submit':
        meaning = '待提交';
        style = {
          borderTop: '0.04rem solid rgba(166, 166, 192, 1)',
          borderRadius: '0 0.16rem 0 0.16rem',
        };
        break;
      case 'approved':
        meaning = '审批通过';
        style = {
          borderTop: '0.04rem solid rgba(0, 191, 165, 1)',
          borderRadius: '0 0.16rem 0 0.16rem',
        };
        break;
      case 'waiting_approve':
        meaning = '待审批';
        style = {
          borderTop: '0.04rem solid rgba(253,174,84,1)',
          borderRadius: '0 0.16rem 0 0.16rem',
        };
        break;
      case 'rejected':
        meaning = '审批拒绝';
        style = {
          borderTop: '0.04rem solid rgba(247, 122, 112, 1)',
          borderRadius: '0 0.16rem 0 0.16rem',
        };
        break;
      default:
        meaning = '待提交';
        style = {
          borderTop: '0.04rem solid rgba(166, 166, 192, 1)',
          borderRadius: '0 0.16rem 0 0.16rem',
        };
    }
    return { meaning, style };
  };

  const handleDelete = () => {
    Modal.open({
      key: Modal.key(),
      title: formatMessage({ id: 'confirm.delete' }),
      children: formatMessage({ id: `${intlPrefix}.view.deleteDocument` }, { applicationNo }),
      okText: formatMessage({ id: 'delete' }),
      okProps: { color: 'red' },
      cancelProps: { color: 'dark' },
      onOk: () => onDelete(id),
      footer: ((okBtn, cancelBtn) => (
        <React.Fragment>
          {cancelBtn}{okBtn}
        </React.Fragment>
      )),
      movable: false,
    });
  };

  // function handleDelete() {
  //   const mProps = {
  //     title: formatMessage({ id: 'confirm.delete' }),
  //     children: formatMessage({ id: `${intlPrefix}.view.deleteDocument` }, { applicationNo }),
  //     okText: formatMessage({ id: 'delete' }),
  //     okProps: { color: 'red' },
  //     cancelProps: { color: 'dark' },
  //   };
  //   dataSet.delete(record, mProps);
  // }
  const renderAction = () => {
    const actionData = [{
      service: [],
      text: formatMessage({ id: 'delete' }),
      action: handleDelete,
    }];
    return (
      <Action
        placement="bottomRight"
        data={actionData}
      />
    );
  };


  return (
    <div className="cluster-apply-card" style={getStatusMeaning().style}>
      {/* <div className={`bg-color bg-color-${state}`} /> */}
      <div className="card-header">
        <span>{formatMessage({ id: `${intlPrefix}.model.applicationNo` })}：</span>
        <span className="card-header-applicationNo" onClick={state === 'waiting_submit' ? () => onUpdate(id, applicationNo) : () => onViewDetail(id, applicationNo)}>
          {/* <Tooltip title={formatMessage({ id: `${intlPrefix}.model.applicationNo` })} placement="top"> */}
          {applicationNo}
          {/* </Tooltip> */}
        </span>
        <span className={`card-header-status status-${state}`}>{getStatusMeaning().meaning}</span>
        {/* <div className="cro_left_bottom" />
        <div className="cro_right_bottom" /> */}
      </div>
      <div className="card-linear" />
      <div className="card-footer">
        <Row>
          <Col span={16}>
            <header>
              <UserAvatar
                user={{
                  loginName: user && user.loginName,
                  realName: user && user.realName,
                  imageUrl: user && user.imageUrl,
                }}
                size="0.36rem"
                hiddenText
              />
              <div className="card-footer-header-content">
                <div className="card-footer-header-content-realName">{user ? user.realName : formatMessage({ id: 'none' })}</div>
                <div>{user ? user.loginName : formatMessage({ id: 'none' })}</div>
              </div>
            </header>
            <footer>
              <div className="card-footer-footer-item" >
                <Tooltip title={formatMessage({ id: `${intlPrefix}.model.applicationDate` })} mouseEnterDelay={0.5}>
                  <Icon type="date_range" />
                </Tooltip>
                <span>{renderTime(applicationDate)}</span>
              </div>
              <div className="card-footer-footer-item">
                <Tooltip title={formatMessage({ id: `${intlPrefix}.model.applicationPurpose` })} mouseEnterDelay={0.5}>
                  <Icon type="category-o" />
                </Tooltip>
                <span>{getPurposeName(applicationPurpose)}</span>
              </div>
              {state === 'waiting_submit' && (
                <div>
                  {renderAction()}
                </div>
              )}
            </footer>
          </Col>
          <Col span={8} className="card-footer-right">
            <div>
              {formatMessage({ id: `${intlPrefix}.model.currentNode` })}
            </div>
            <span className={`card-footer-right-node text text-${state}`}>
              <span className={`card-footer-right-dot card-footer-right-dot-${state}`} />
              <Tooltip title={currentActivity && currentActivity.activityName} placement="top">
                <span className="card-footer-right-name">{(currentActivity && currentActivity.activityName) || '--'}</span>
              </Tooltip>
            </span>
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default Card;
