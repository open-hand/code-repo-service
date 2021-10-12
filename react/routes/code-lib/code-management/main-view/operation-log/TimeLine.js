import React, { useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon, Button } from 'choerodon-ui';
import { Spin } from 'choerodon-ui/pro';
import { Loading } from '@choerodon/components';

const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '18px',
  textAlign: 'center',
};

const TimeLine = ({
  isMore, opEventTypeLookupData, loadData, listViewDs,
}) => {
  const record = listViewDs.current && listViewDs.toData();

  const getOpEventTypeMeaning = useCallback((code) => {
    let icon;
    let style;
    switch (code) {
      case 'ADD_MEMBER':
        icon = 'account_circle';
        break;
      case 'UPDATE_MEMBER':
        icon = 'rate_review1';
        style = { background: 'rgba(81,79,160,1)' };
        break;
      case 'REMOVE_MEMBER':
        icon = 'delete';
        style = { background: 'rgba(244,133,144,1)' };
        break;
      case 'REMOVE_EXPIRED_MEMBER':
        icon = 'date_range-o';
        style = { background: 'rgba(244,133,144,1)' };
        break;
      default:
        icon = 'sync_user';
    }
    return { ...opEventTypeLookupData.find(o => o.value === code), icon, style };
  }, [opEventTypeLookupData]);

  // 更多操作
  function loadMoreOptsRecord() {
    loadData(listViewDs.currentPage + 1);
  }

  const getUserIcon = (imageUrl, name) => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" />;
    }
    return <div className="code-lib-opreation-log-timeLine-card-content-text-div-icon">{name[0]}</div>;
  };

  const getRepositoryIcon = useCallback((imageUrl, name) => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" style={imgStyle} />;
    }
    return <div style={iconStyle}>{name[0]}</div>;
  });

  function renderData() {
    return record ? (
      <ul>
        {
          record.map((item, index) => {
            const {
 id, opDate, opEventType, opContent, repositoryName, opUserImageUrl, opUserName, repositoryImageUrl,
} = item;
            const [date, time] = opDate.split(' ');
            return (
              <li key={id}>
                <div className="code-lib-opreation-log-timeLine-card">
                  <div className="code-lib-opreation-log-timeLine-card-header">
                    <div className="code-lib-opreation-log-timeLine-card-header-icon">
                      <div style={{ display: 'flex' }}>
                        <Icon type={getOpEventTypeMeaning(opEventType).icon} style={getOpEventTypeMeaning(opEventType).style} />
                        <span className="code-lib-opreation-log-timeLine-card-header-title">{getOpEventTypeMeaning(opEventType).meaning}</span>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', marginLeft: '32px' }}>
                        {getRepositoryIcon(repositoryImageUrl, repositoryName)}
                        <span className="code-lib-opreation-log-timeLine-card-header-project">{repositoryName}</span>
                      </div>
                    </div>
                    <div className="code-lib-opreation-log-timeLine-card-header-date">
                      <Icon type="date_range" />
                      <span style={{ marginLeft: '0.15rem' }}>{date}</span>
                    </div>
                  </div>
                  <div className="code-lib-opreation-log-timeLine-card-content">
                    <div className="code-lib-opreation-log-timeLine-card-content-text">
                      {getUserIcon(opUserImageUrl, opUserName)}
                      <p>{opContent}</p>
                    </div>
                    <div className="code-lib-opreation-log-timeLine-card-content-time"><Icon type="av_timer" /><span style={{ marginLeft: '0.15rem' }}>{time}</span></div>
                  </div>
                  {index !== record.length - 1 && <div className="code-lib-opreation-log-timeLine-card-line" />}
                </div>
              </li>
            );
          })
        }
      </ul>
    ) : null;
  }

  return (

    <Loading dataSet={listViewDs} type="c7n">
      <div className="code-lib-opreation-log-timeLine">
        {
          record && record.length > 0 ? (
            <div className="code-lib-opreation-log-timeLine-body">
              {renderData()}
            </div>
          ) : (
            <div className="code-lib-opreation-log-timeLine-no-content">
              <span>暂无操作记录</span>
            </div>)
        }
        {isMore && <Button type="primary" onClick={loadMoreOptsRecord}>加载更多</Button>}
      </div>
    </Loading>

  );
};

export default observer(TimeLine);
