/**
* 权限审批-组织层
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
/* eslint-disable */
import React, { useEffect, useState } from 'react';
import { Content, axios, Choerodon } from '@choerodon/boot';
import { Table } from 'choerodon-ui/pro';
import { Tooltip, Row, Col, Icon } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import TimeAgo from 'timeago-react';
import { isNil } from 'lodash';
import moment from 'moment';
import UserAvatar from '@/components/user-avatar';
import './index.less';

import { TabKeyEnum,useStore } from '../stores';

const intlPrefix = 'infra.codeManage.ps';

const { Column } = Table;
const PsAudit = ({ psAuditDs, activeProject, activeTabKey, organizationId }) => {

  const {formatClient} = useStore();

  const [executionDate, setExecutionDate] = useState(undefined);
  async function fetchExecutionDate() {
    const projectId = activeProject.id === 'all' ? undefined : activeProject.id;
    const params = projectId ? `?projectId=${projectId}` : '';
    await axios.get(`/rducm/v1/organizations/${organizationId}/projects/gitlab/member-audit-logs/detail/latest${params}`)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
          return false;
        } else {
          const dataStr = res.auditEndDate ? moment(res.auditEndDate).format('YYYY-MM-DD HH:mm:ss') : undefined;
          setExecutionDate(dataStr);
          return true;
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        return false;
      });
  }

  useEffect(() => {
    if (activeProject.id && activeTabKey === TabKeyEnum.PSAUDIT) {
      if (activeProject.id !== 'all') {
        psAuditDs.setQueryParameter('projectIds', activeProject.id.toString());
      } else if (activeProject.id === 'all') {
        psAuditDs.setQueryParameter('projectIds', undefined);
      }
      psAuditDs.query();
      fetchExecutionDate();
    }
  }, [activeProject, activeTabKey]);

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function renderTime() {
    const date = isNil(executionDate) ? '无' : (
      <Tooltip
        title={executionDate}
      >
        <TimeAgo
          datetime={executionDate}
          locale={Choerodon.getMessage('zh_CN', 'en')}
        />
      </Tooltip>
    );
    return date;
  }

  function renderName({ record }) {
    const avatar = (
      <UserAvatar
        user={{
          id: record.get('user').userId,
          loginName: record.get('user').loginName,
          realName: record.get('user').realName,
          imageUrl: record.get('user').imageUrl,
          email: record.get('user').email,
        }}
        // hiddenText
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
      </div>
    );
  }
  function renderLoginName({ record }) {
    return (
      <span>{record.get('user').loginName}</span>
    );
  }
  function renderProject({ record }) {
    // return (
    //   <Tooltip mouseEnterDelay={0.5} title={record.get('project') ? record.get('project').projectName : ''}>{record.get('project') ? record.get('project').projectName : ''}</Tooltip>
    // );
    const avatar = (
      <UserAvatar
        user={{
          id: record.get('project').projectId,
          loginName: record.get('project').projectCode,
          realName: record.get('project').projectName,
          imageUrl: record.get('project').imageUrl,
        }}
        style={{ maxWidth: '1.5rem' }}
        // hiddenText
      />
    );
    return (
      <div style={{ display: 'inline-flex' }}>
        {avatar}
      </div>
    );
  }
  function renderLevel({ text, record }) {
    return <span style={{ color: !record.get('accessLevelSyncFlag') ? '#EF4E42' : '' }}>{text}</span>;
  }
  function renderDate({ text, record }) {
    return <span style={{ color: !record.get('expiresAtSyncFlag') ? '#EF4E42' : '' }}>{text || '-'}</span>;
  }

  return (
    <Content style={{ paddingTop: 0, height: 'calc(100% - 95px)', marginTop: 0 }} className="c7n-infra-code-management-org" >
      <div >
        <Row className="c7n-infra-code-management-org-tip-text">
          <Col span={8}>
            <Icon type="date_range" style={{ marginRight: '.03rem', marginBottom: '.04rem' }} />
            <span>{formatClient({ id: `audit.totalVarianceData` })}<span className="c7n-infra-code-management-org-tip-text-date">{renderTime()}</span></span>
          </Col>
          <Col span={8}>
            <Icon type="compare" style={{ marginRight: '.03rem' }} />
            <span>{formatClient({ id: `audit.auditExecutionDate` })}<span className="c7n-infra-code-management-org-tip-text-number">{psAuditDs.totalCount || 0}</span></span>
          </Col>
        </Row>
      </div>
      <Table
        dataSet={psAuditDs}
        filter={handleTableFilter}
        queryBar="bar"
        queryFieldsLimit={3}
      >
        <Column name="realName" renderer={renderName} width={150} />
        <Column name="loginName" renderer={renderLoginName} />
        <Column name="project" width={180} renderer={renderProject} />
        <Column name="repositoryName" width={180} />
        <Column name="accessLevel" renderer={renderLevel} />
        <Column name="expiresAt" renderer={renderDate} />
        <Column name="glAccessLevel" renderer={renderLevel} />
        <Column name="glExpiresAt" renderer={renderDate} />
      </Table>
    </Content>
  );
};

export default observer(PsAudit);
