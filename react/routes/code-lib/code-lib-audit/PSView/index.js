/**
* 代码库权限查看-组织层
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/3/26
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback } from 'react';
import { Content } from '@choerodon/boot';
import { Table } from 'choerodon-ui/pro';
import { Tooltip } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import TimePopover from '@/components/time-popover/TimePopover';
import renderFullName from '@/utils/renderer';
import { TabKeyEnum } from '../stores';


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
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '18px',
  textAlign: 'center',
  flexShrink: 0,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
};

const { Column } = Table;
const PsView = ({ psViewDs, activeProject, activeTabKey }) => {
  useEffect(() => {
    if (activeProject.id && activeTabKey === TabKeyEnum.PSVIEW) {
      if (activeProject.id !== 'all') {
        psViewDs.setQueryParameter('projectIds', activeProject.id);
      } else if (activeProject.id === 'all') {
        psViewDs.setQueryParameter('projectIds', undefined);
      }
      psViewDs.query();
    }
  }, [activeProject, activeTabKey]);

  function renderTime({ text }) {
    return isNil(text) ? '' : <TimePopover content={text} />;
  }

  function renderName({ record }) {
    const text = record.get('user').realName;
    if (record.get('syncGitlabFlag')) {
      return (
        <Tooltip title={text}>
          {text}
        </Tooltip>
      );
    } else {
      return (
        <Tooltip title={text}>
          <div style={{ display: 'flex' }}>
            <div style={{ display: 'inline-block', maxWidth: '1.2rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {text}
            </div>
            <div className="assign-member-external-user">
              <span className="assign-member-external-user-text">
                未同步
              </span>
            </div>
          </div>
        </Tooltip>
      );
    }
  }

  function renderRole({ value }) {
    return value.join();
  }

  const rendererIcon = useCallback((imageUrl, text) => {
    let iconElement;
    if (imageUrl) {
      iconElement = <img src={imageUrl} alt="" style={imgStyle} />;
    } else {
      iconElement = <div style={iconStyle}>{text[0]}</div>;
    }
    return (
      <Tooltip title={text}>
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
      </Tooltip>
    );
  }, []);

  return (
    <Content style={{ paddingTop: 0, height: 'calc(100% - 95px)' }}>
      <Table
        className="no-border-top-table"
        dataSet={psViewDs}
        queryBar="bar"
      >
        <Column name="realName" renderer={renderName} />
        <Column
          name="loginName"
          renderer={({ record }) =>
            (
              <Tooltip title={record.get('user').loginName}>
                {record.get('user').loginName}
              </Tooltip>
            )
          }
        />
        <Column
          name="projectName"
          renderer={({ record }) => rendererIcon(record.get('project').imageUrl, record.get('project').projectName)}
        />
        <Column name="repositoryName" renderer={renderFullName} />
        <Column name="roleNames" renderer={renderRole} />
        <Column name="glAccessLevel" />
        <Column name="glExpiresAt" />
        <Column
          name="createdByName"
          renderer={({ record }) => record.get('createdUser').imageUrl && record.get('createdUser').realName && rendererIcon(record.get('createdUser').imageUrl, record.get('createdUser').realName)}
        />
        <Column name="creationDate" renderer={renderTime} />
      </Table>
    </Content>
  );
};

export default observer(PsView);
