/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/05/07
 * @copyright 2020 ® HAND
 */
import React, { useEffect, useState } from 'react';
import { Spin } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import classnames from 'classnames';
import { Stores } from 'choerodon-ui/pro';
import uuidv4 from 'uuid/v4';
import { Icon, Steps, Tooltip } from 'choerodon-ui';
import TimePopover from '@/components/time-popover/TimePopover';
import UserAvatar from '@/components/user-avatar';

import './index.less';

const { Step } = Steps;

const ApplyDetail = observer(({ getPurposeName, applicationNo, dataSet, AppState, intlPrefix, formatMessage }) => {
  const [his, setHis] = useState([]);
  const [stateLookupData, setStateLookupData] = useState([]);


  function refresh() {
    dataSet.query();
  }

  useEffect(() => {
    refresh();
    async function init() {
      const { currentMenuType: { projectId } } = AppState;
      const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUEM.CAF_STATE');
      const workflowHis = await axios.get(`/rduem/v1/cluster-application-forms-query/${projectId}/activity/his?applicationNo=${applicationNo}`);

      setHis(workflowHis);
      setStateLookupData(lookupData);
    }
    init();
  }, []);

  if (!dataSet.current) return;
  const detailData = dataSet.current.toData();
  const {
    // id,
    user: applyUser,
    applicationDate,
    applicationDesc,
    applicationList,
    applicationPurpose,
    opsProjectName,
    deadlineDate,
    state,
  } = detailData;
  const { id: userId, loginName, realName, imageUrl } = applyUser;

  const renderStep = () => his.map(item => {
    const { user, activityName, operateType, operateTypeMeaning, message, endTime } = item;
    const titleDom = (
      <React.Fragment>
        <div>{formatMessage({ id: 'approval.result' })}: {operateTypeMeaning}</div>
        <div>{formatMessage({ id: 'approval.message' })}: {message}</div>
      </React.Fragment>
    );
    const desDom = (
      <React.Fragment>
        <div style={{ cursor: 'pointer' }}>
          <Tooltip
            title={(operateType === 'approve' || operateType === 'reject') ? titleDom : ''}
          >
            {activityName}
          </Tooltip>
        </div>
        <UserAvatar
          user={{
            id: user && user.userId,
            loginName: user && user.loginName,
            realName: user && user.realName,
            imageUrl: user && user.imageUrl,
          }}
          color="rgba(0, 0, 0, 0.65)"
          size="0.2rem"
          style={{ maxWidth: '1.5rem', marginRight: '0.12rem', marginTop: '0.08rem' }}
        />
        {endTime && (
          <div style={{ display: 'flex', color: 'rgba(0, 0, 0, 0.65)', fontSize: '0.12rem' }}>
            <Icon type="date_range-o" style={{ fontSize: '0.14rem', margin: '0.03rem 0.05rem 0 0' }} />
            <span><TimePopover content={endTime} /></span>
          </div>
        )}
      </React.Fragment>
    );
    return (
      <Step status="finish" title={desDom} />
    );
  });

  const getState = () => {
    const stateMeaning = (stateLookupData.find(o => o.value === state) || {}).meaning;
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
      <div style={{ color: getTagColor(state) }} className="env-cluster-detail-modal-user-state-value">
        <span className={`cluster-apply-detail-status-dot cluster-apply-detail-status-dot-${state}`} />
        <span style={{ marginLeft: '0.05rem' }}>{stateMeaning}</span>
      </div>
    );
  };

  const getServiceCard = (infrastructure) => (applicationList[infrastructure] || []).map((o) => (
    <div key={uuidv4()} className="env-cluster-detail-modal-content-second-card">
      <span className="env-cluster-detail-modal-content-second-card-title">{o.name}</span>
      <div style={{ marginTop: '0.16rem' }}>
        <div style={{ display: 'inline-block', width: '50%' }}>
          <div className="env-cluster-detail-modal-content-second-card-label">{formatMessage({ id: 'version' })}</div>
          <div className="env-cluster-detail-modal-content-second-card-value">{o.version}</div>
        </div>

        <div style={{ display: 'inline-block', width: '50%' }}>
          <div className="env-cluster-detail-modal-content-second-card-label">{formatMessage({ id: `${intlPrefix}.model.configuration` })}</div>
          <div className="env-cluster-detail-modal-content-second-card-value">{o.configuration}</div>
        </div>
      </div>

      <div className="env-cluster-detail-modal-content-second-card-desc">
        {o.desc}
      </div>
    </div>
  ));

  return (
    <Spin dataSet={dataSet} >
      <div className="env-cluster-detail-modal">
        <div className="env-cluster-detail-modal-top">
          <div className="env-cluster-detail-modal-user">
            <div className="env-cluster-detail-modal-user-label">{formatMessage({ id: 'applicant' })}</div>
            <div className="env-cluster-detail-modal-user-value">
              <UserAvatar
                user={{
                  id: userId,
                  loginName,
                  realName,
                  imageUrl,
                }}
                color="rgba(0, 0, 0, 1)"
                size="0.2rem"
                style={{ maxWidth: '2rem' }}
              />
            </div>
            <div className="env-cluster-detail-modal-user-state-label">{formatMessage({ id: 'status' })}</div>
          </div>

          <div className="env-cluster-detail-modal-user" style={{ marginTop: '0.12rem' }}>
            <div className="env-cluster-detail-modal-user-label">{formatMessage({ id: `${intlPrefix}.model.applicationDate` })}</div>
            <div className="env-cluster-detail-modal-user-value">
              {applicationDate}
            </div>
            <div className="env-cluster-detail-modal-user-state-label">
              {getState()}
            </div>
          </div>
        </div>
        <div className="env-cluster-detail-modal-content">
          {/* 1. 流程进度 */}
          <div className="env-cluster-detail-modal-content-card" style={{ marginTop: 0 }}>
            <div className="env-cluster-detail-modal-content-card-title">
              {formatMessage({ id: `${intlPrefix}.view.flow.schedule` })}
            </div>
            <div className="env-cluster-detail-modal-divider" />
            <div className="env-cluster-detail-modal-content-card-content" style={{ padding: '0.15rem 0.16rem' }}>
              <Steps progressDot>
                {renderStep()}
              </Steps>
              {/* <Timeline>
                {his.map(item => {
                  const { id, user, activityName, operateType, operateTypeMeaning, message, endTime } = item;
                  return (
                    <Timeline.Item key={id} value={id} color={getHisTagColor(operateType)}>
                      <div style={{ display: 'inline-flex' }}>
                        <span>
                          <UserAvatar
                            user={{
                              id: user && user.userId,
                              loginName: user && user.loginName,
                              realName: user && user.realName,
                              imageUrl: user && user.imageUrl,
                            }}
                            color="rgba(0, 0, 0, 1)"
                            size="0.2rem"
                            style={{ maxWidth: '2rem', marginRight: '0.12rem' }}
                          />
                        </span>
                        <span style={{ marginRight: '0.12rem' }}>{activityName}</span>
                        <span style={{ marginRight: '0.12rem' }}>{operateTypeMeaning}</span>
                        <span style={{ marginRight: '0.12rem' }}>{message}</span>
                      </div>
                      {endTime && (
                        <div style={{ display: 'flex', color: 'rgba(0, 0, 0, 0.65)' }}>
                          <Icon type="date_range-o" style={{ marginRight: '0.05rem' }} />
                          <span><TimePopover content={endTime} /></span>
                        </div>
                      )}
                    </Timeline.Item>
                  );
                })}
              </Timeline> */}
              {/* <div className="env-cluster-detail-modal-content-card-content-progress">
                <div style={{ display: 'flex', alignItems: 'center', padding: '0 0.38rem 0 0.32rem' }}>
                  {his.length > 0 && getProgressLine()}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '0.14rem', padding: '0 0.14rem' }}>
                  {his.length > 0 && getProgress()}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', marginTop: '0.12rem', padding: '0 0.14rem 0.2rem' }}>
                  {his.length > 0 && getHisApplicationUser()}
                </div>
              </div> */}
            </div>
          </div>

          {/* 2. 申请信息 */}
          <div className="env-cluster-detail-modal-content-card">
            <div className="env-cluster-detail-modal-content-card-title">
              {formatMessage({ id: 'applyInfo' })}
            </div>
            <div className="env-cluster-detail-modal-divider" />
            <div className="env-cluster-detail-modal-content-card-content" style={{ padding: '0.15rem 0.16rem 0.24rem' }}>
              <div>
                <div className="env-cluster-detail-modal-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-cluster-detail-modal-item-label">{formatMessage({ id: `${intlPrefix}.model.applicationPurpose` })}</div>
                  <div className="env-cluster-detail-modal-item-value">
                    {getPurposeName(applicationPurpose)}
                  </div>
                </div>
                <div className="env-cluster-detail-modal-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-cluster-detail-modal-item-label">{formatMessage({ id: `${intlPrefix}.model.opsProject` })}</div>
                  <div className="env-cluster-detail-modal-item-value">
                    {opsProjectName}
                  </div>
                </div>
              </div>
              <div style={{ marginTop: '0.16rem', marginBottom: '0.1rem' }}>
                <div className="env-cluster-detail-modal-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-cluster-detail-modal-item-label">{formatMessage({ id: `${intlPrefix}.model.deadlineDate` })}</div>
                  <div className="env-cluster-detail-modal-item-value">
                    {deadlineDate}
                  </div>
                </div>
                <div className="env-cluster-detail-modal-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-cluster-detail-modal-item-label">{formatMessage({ id: 'description' })}</div>
                  <div className="env-cluster-detail-modal-item-value">
                    {applicationDesc}
                  </div>
                </div>
              </div>

              <div className={classnames('env-cluster-detail-modal-content-card-title', 'env-cluster-detail-modal-content-card-second-title')}>
                {formatMessage({ id: `${intlPrefix}.view.infrastructure` })}
              </div>

              <div className="env-cluster-detail-modal-content-card-flow" >
                {applicationList && getServiceCard('infrastructure')}
              </div>

              <div className={classnames('env-cluster-detail-modal-content-card-title', 'env-cluster-detail-modal-content-card-second-title')}>
                {formatMessage({ id: `${intlPrefix}.view.platform` })}
              </div>

              <div className="env-cluster-detail-modal-content-card-flow" >
                {applicationList && getServiceCard('platformService')}
              </div>

              <div className={classnames('env-cluster-detail-modal-content-card-title', 'env-cluster-detail-modal-content-card-second-title')}>
                {formatMessage({ id: `${intlPrefix}.view.others` })}
              </div>

              <div className="env-cluster-detail-modal-content-second-card-desc">
                {applicationList && applicationList.others && applicationList.others.desc}
              </div>
            </div>
          </div>
        </div>
      </div>
    </Spin >
  );
});

export default ApplyDetail;
