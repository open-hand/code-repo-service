import React, { useEffect, useState } from 'react';
import { axios, stores, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { Stores } from 'choerodon-ui/pro';
import TimeAgo from 'timeago-react';
import { Tooltip, Icon } from 'choerodon-ui';
import uuidv4 from 'uuid/v4';
import classnames from 'classnames';

// const intlPrefix = 'infra.env.approval';

const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
  border: '1px solid rgba(104,135,232,1)',
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '16px',
  textAlign: 'center',
  border: '1px solid rgba(104,135,232,1)',
};


const ApplicationForm = ({ data, sendToParent }) => {
  const [detail, setDetail] = useState({});
  const [his, setHis] = useState([]);
  const [stateLookupData, setStateLookupData] = useState([]);
  const [purposeLookupData, setPurposeLookupData] = useState([]);
  useEffect(() => {
    async function init() {
      const { currentMenuType: { projectId } } = stores.AppState;
      const [result, lookupData, workflowHis, lookupData2] = await Promise.all([
        axios.get(`/rduem/v1/cluster-application-forms-query/${projectId}/approve/${data.id}`),
        Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUEM.CAF_STATE'),
        axios.get(`/rduem/v1/cluster-application-forms-query/${projectId}/activity/his?applicationNo=${data.applicationNo}`),
        Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUEM.CAF_PURPOSE'),
      ]);

      setHis(workflowHis);
      setStateLookupData(lookupData);
      setPurposeLookupData(lookupData2);
      setDetail(result);
      // eslint-disable-next-line no-unused-expressions
      sendToParent && sendToParent(result);
    }
    init();
  }, []);

  const getUserIcon = (imageUrl = '', name = '') => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" style={imgStyle} />;
    } else {
      return <div style={iconStyle}>{name[0]}</div>;
    }
  };

  const getState = () => {
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
    const stateMeaning = (stateLookupData.find(o => o.value === detail.state) || {}).meaning;
    return (
      <div style={{ color: getTagColor(detail.state) }} className="env-application-form-user-state-value">
        <span className="env-application-form-user-state-label-circle" style={{ background: getTagColor(detail.state) }} />
        <span style={{ marginLeft: '5px' }}>{stateMeaning}</span>
      </div>
    );
  };

  const getProgressLine = () => {
    const arr = [];
    // eslint-disable-next-line no-plusplus
    for (let i = 0; i < his.length; i++) {
      if (i !== his.length - 1) {
        arr.push(<span key={uuidv4()} className="env-application-form-content-card-content-progress-circle" style={{ marginLeft: '6px' }} />);
        // 计算线的长度，8为点的宽度，6为点和线的间距
        arr.push(<span
          key={uuidv4()}
          className="env-application-form-content-card-content-progress-line"
          // eslint-disable-next-line no-mixed-operators
          style={{ marginLeft: '6px', width: `calc(100% - ${8 * his.length}px - ${6 * (his.length * 2 - 2)}px)` }}
        />);
      } else {
        arr.push(<span key={uuidv4()} className="env-application-form-content-card-content-progress-circle" style={{ marginLeft: '6px' }} />);
      }
    }
    return arr;
  };

  const getProgress = () => his.map((o, index) =>
    (
      <React.Fragment>
        {(index === 0 || index === his.length - 1) ?
          <div key={o.id}>{o.activityName}</div>
          :
          <Tooltip title={
            <React.Fragment>
              <div>
                审批结果：{o.operateTypeMeaning}
              </div>
              <div>
                审批意见：{o.message}
              </div>
            </React.Fragment>
          }
          >
            <div key={o.id}>{o.activityName}</div>
          </Tooltip>
        }
      </React.Fragment>
    ));

  const getHisApplicationUser = () => {
    const getPosition = (index) => {
      const style = { display: 'flex', alignItems: 'center' };
      if (index === 0) {
        style.justifyContent = 'start';
      } else if (index === his.length - 1) {
        style.justifyContent = 'start';
        style.flexDirection = 'row-reverse';
      } else {
        style.justifyContent = 'center';
      }
      return style;
    };
    return his.map((o, index) =>
      (
        <div key={o.id} style={{ width: `${100 / his.length}%` }}>
          {o.user ?
            <div style={getPosition(index)}>
              {/* 多加一层是因为加了row-reverse，加row-reverse是为了让文字靠右排列 */}
              <div style={{ display: 'flex', alignItems: 'center' }}>
                {getUserIcon(o.user.imageUrl, o.user.realName)}
                <span style={{ marginLeft: '5px', color: 'rgba(0, 0, 0, 0.65)' }} >{o.user.realName}</span>
              </div>
            </div>
            :
            <div style={{ height: '19px' }} />
          }
          <div style={{ ...getPosition(index), marginTop: '6px' }}>
            {/* 多加一层是因为加了row-reverse，加row-reverse是为了让文字靠右排列 */}
            <div style={{ display: 'flex', alignItems: 'center', color: 'rgba(0, 0, 0, 0.65)' }}>
              <Icon type="date_range-o" style={{ marginRight: '5px' }} />
              <Tooltip placement="top" title={o.startTime || ''}>
                <TimeAgo datetime={o.startTime || ''} locale={Choerodon.getMessage('zh_CN', 'en')} />
              </Tooltip>
            </div>
          </div>
        </div>
      ));
  };

  const getServiceCard = (infrastructure) => (detail.applicationList[infrastructure] || []).map((o) => (
    <div key={uuidv4()} className="env-application-form-content-second-card">
      <span className="env-application-form-content-second-card-title">{o.name}</span>
      <div style={{ marginTop: '16px' }}>
        <div style={{ display: 'inline-block', width: '50%' }}>
          <div className="env-application-form-content-second-card-label">版本</div>
          <div className="env-application-form-content-second-card-value">{o.version}</div>
        </div>

        <div style={{ display: 'inline-block', width: '50%' }}>
          <div className="env-application-form-content-second-card-label">期望配置</div>
          <div className="env-application-form-content-second-card-value">{o.configuration}</div>
        </div>
      </div>

      <div className="env-application-form-content-second-card-desc">
        {o.desc}
      </div>
    </div>
  ));

  return (
    <React.Fragment>
      <div className="env-application-form">
        <div className="env-application-form-top">
          <div className="env-application-form-user">
            <div className="env-application-form-user-label">申请人</div>
            <div className="env-application-form-user-value">
              {getUserIcon(detail.user && detail.user.imageUrl, detail.user && detail.user.realName)}
              <span style={{ marginLeft: '5px' }}>{`${detail.user && detail.user.realName}(${detail.user && detail.user.userId})`}</span>
            </div>
            <div className="env-application-form-user-state-label">状态</div>
          </div>

          <div className="env-application-form-user" style={{ marginTop: '12px' }}>
            <div className="env-application-form-user-label">申请日期</div>
            <div className="env-application-form-user-value">
              {detail.applicationDate}
            </div>
            <div className="env-application-form-user-state-label">
              {getState()}
            </div>
          </div>
        </div>
        <div className="env-application-form-content">
          <div className="env-application-form-content-card">
            <div className="env-application-form-content-card-title">
              流程进度
            </div>
            <div className="env-application-form-divider" />
            <div className="env-application-form-content-card-content">
              <div className="env-application-form-content-card-content-progress">
                <div style={{ display: 'flex', alignItems: 'center', padding: '0 38px 0 32px' }}>
                  {his.length > 0 && getProgressLine()}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '14px', padding: '0 14px' }}>
                  {his.length > 0 && getProgress()}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', marginTop: '12px', padding: '0 14px 20px' }}>
                  {his.length > 0 && getHisApplicationUser()}
                </div>
              </div>
            </div>
          </div>


          <div className="env-application-form-content-card">
            <div className="env-application-form-content-card-title">
              申请信息
            </div>
            <div className="env-application-form-divider" />
            <div className="env-application-form-content-card-content" style={{ padding: '15px 16px 24px' }}>


              <div>
                <div className="env-application-form-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-application-form-item-label">申请用途</div>
                  <div className="env-application-form-item-value">
                    {(purposeLookupData.find(o => o.value === detail.applicationPurpose) || {}).meaning}
                  </div>
                </div>

                <div className="env-application-form-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-application-form-item-label">运维项目</div>
                  <div className="env-application-form-item-value">
                    {detail.opsProjectName}
                  </div>
                </div>
              </div>

              <div style={{ marginTop: '16px' }}>
                <div className="env-application-form-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-application-form-item-label">集群使用期限</div>
                  <div className="env-application-form-item-value">
                    {detail.deadlineDate}
                  </div>
                </div>

                <div className="env-application-form-item" style={{ width: '50%', display: 'inline-flex' }}>
                  <div className="env-application-form-item-label">描述</div>
                  <div className="env-application-form-item-value">
                    {detail.applicationDesc}
                  </div>
                </div>
              </div>


              <div className={classnames('env-application-form-content-card-title', 'env-application-form-content-card-second-title')}>
                基础设施
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap' }}>
                {detail.applicationList && getServiceCard('infrastructure')}
              </div>

              <div className={classnames('env-application-form-content-card-title', 'env-application-form-content-card-second-title')}>
                平台服务
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap' }}>
                {detail.applicationList && getServiceCard('platformService')}
              </div>

              <div className={classnames('env-application-form-content-card-title', 'env-application-form-content-card-second-title')}>
                其他
              </div>

              <div className="env-application-form-content-second-card-desc">
                {detail.applicationList && detail.applicationList.others && detail.applicationList.others.desc}
              </div>
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default observer(ApplicationForm);
