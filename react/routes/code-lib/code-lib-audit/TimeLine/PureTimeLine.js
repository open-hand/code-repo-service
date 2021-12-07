import React, { useCallback } from 'react';
import { useFormatMessage } from '@choerodon/master';
import { observer } from 'mobx-react-lite';
import { Icon, Button } from 'choerodon-ui';
import { Spin } from 'choerodon-ui/pro';

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
  isMore, opEventTypeLookupData, loadData, optLogDs,
}) => {
  const record = optLogDs.current && optLogDs.toData();

  const formatClient = useFormatMessage('c7ncd.code-lib-org');

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
    loadData(optLogDs.currentPage + 1);
  }

  const getProjectIcon = useCallback((imageUrl, name) => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" style={imgStyle} />;
    }
    return <div style={iconStyle}>{name[0]}</div>;
  });

  const getUserIcon = (imageUrl, name) => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" />;
    }
    return <div className="code-lib-audit-optlog-timeLine-card-content-text-div-icon">{name[0]}</div>;
  };

  function renderData() {
    return record ? (
      <ul>
        {
          record.map((item, index) => {
            const {
 id, opDate, opEventType, opContent, repositoryImageUrl, repositoryName, opUserImageUrl, project,
} = item;
            const [date, time] = opDate.split(' ');
            return (
              <li key={id}>
                <div className="code-lib-audit-optlog-timeLine-card">
                  <div className="code-lib-audit-optlog-timeLine-card-header">
                    <div className="code-lib-audit-optlog-timeLine-card-header-icon">
                      <div style={{ display: 'flex' }}>
                        <Icon
                          type={getOpEventTypeMeaning(opEventType).icon}
                          style={getOpEventTypeMeaning(opEventType).style}
                        />
                        <span className="code-lib-audit-optlog-timeLine-card-header-title">{getOpEventTypeMeaning(opEventType).meaning}</span>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', marginLeft: '32px' }}>
                        {getProjectIcon(project.imageUrl, project.projectName)}
                        <span className="code-lib-audit-optlog-timeLine-card-header-project">{project.projectName}</span>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', marginLeft: '32px' }}>
                        {getProjectIcon(repositoryImageUrl, repositoryName)}
                        <span className="code-lib-audit-optlog-timeLine-card-header-project">{repositoryName}</span>
                      </div>
                    </div>
                    <div className="code-lib-audit-optlog-timeLine-card-header-date">
                      <Icon type="date_range" />
                      <span style={{ marginLeft: '0.15rem' }}>{date}</span>
                    </div>
                  </div>
                  <div className="code-lib-audit-optlog-timeLine-card-content">
                    <div className="code-lib-audit-optlog-timeLine-card-content-text">
                      {getUserIcon(opUserImageUrl, opContent)}
                      <p>{opContent}</p>
                    </div>
                    <div className="code-lib-audit-optlog-timeLine-card-content-time"><Icon type="av_timer" /><span style={{ marginLeft: '0.15rem' }}>{time}</span></div>
                  </div>
                  {index !== record.length - 1 && <div className="code-lib-audit-optlog-timeLine-card-line" />}
                </div>
              </li>
            );
          })
        }
      </ul>
    ) : null;
  }

  return (
    <Spin dataSet={optLogDs}>
      <div className="code-lib-audit-optlog-timeLine">
        {
          record && record.length > 0 ? (
            <div className="code-lib-audit-optlog-timeLine-body">
              {renderData()}
            </div>
          ) :
            (
              <div className="code-lib-audit-optlog-timeLine-no-content">
                <span>
                  {formatClient({ id: 'log.noOperationRecord' })}
                </span>
              </div>)
        }
        {isMore &&
          // eslint-disable-next-line react/jsx-no-bind
          <Button type="primary" onClick={loadMoreOptsRecord}>
            加载更多
          </Button>}
      </div>
    </Spin>
  );
};

export default observer(TimeLine);
