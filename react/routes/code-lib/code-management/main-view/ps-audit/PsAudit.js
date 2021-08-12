/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect } from 'react';
import { Page, Action, Choerodon } from '@choerodon/boot';
import { Table, Modal } from 'choerodon-ui/pro';
import { Tooltip, Row, Col, Icon, message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import TimeAgo from 'timeago-react';
import moment from 'moment';
import UserAvatar from '@/components/user-avatar';
import { usPsManagerStore } from '../stores';
import './index.less';

const { Column } = Table;
const intlPrefix = 'infra.codeManage.ps';

const modalKey = Modal.key();

const PsSet = observer(() => {
  const {
    intl: { formatMessage },
    psAuditDs,
    appId,
    AppState: { currentMenuType: { id: projectId, organizationId } },
    overStores,
    executionDate,
    setExecutionDate,
  } = usPsManagerStore();

  function refresh() {
    psAuditDs.query();
  }

  async function fetchExecutionDate() {
    await overStores.fetchExecutionDate(organizationId, projectId)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
          return false;
        }
        const dataStr = res.auditEndDate ? moment(res.auditEndDate).format('YYYY-MM-DD HH:mm:ss') : undefined;
        setExecutionDate(dataStr);
        return true;
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        return false;
      });
  }

  useEffect(() => {
    refresh();
    fetchExecutionDate();
  }, [appId]);

  async function handleOk(record) {
    const params = {
      organizationId,
      projectId,
      id: record.get('id'),
      repositoryId: record.get('repositoryId'),
    };
    const result = await overStores.asyncPermission(params).then((res) => {
      if (res.failed) {
        message.error(res.message);
        return false;
      }
      refresh();
      message.success(formatMessage({ id: 'infra.codeManage.ps.message.asyncSuccess' }));
      return true;
    })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        return false;
      });
    return result;
  }
  const openDelete = (record) => {
    Modal.open({
      key: modalKey,
      title: formatMessage({ id: `${intlPrefix}.operate.fixPs` }),
      children: formatMessage({ id: `${intlPrefix}.operate.fixPs.confirm` }),
      // okText: formatMessage({ id: 'fix' }),
      // okProps: { color: 'red' },
      // cancelProps: { color: 'dark' },
      onOk: () => handleOk(record),
      footer: ((okBtn, cancelBtn) => (
        <React.Fragment>
          {cancelBtn}
          {okBtn}
        </React.Fragment>
      )),
      movable: false,
    });
  };

  function renderAction({ record }) {
    const actionData = [{
      service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
      text: formatMessage({ id: 'fix' }),
      action: () => openDelete(record),
    }];
    return <Action data={actionData} />;
  }

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function renderTime() {
    const date = isNil(executionDate) ? formatMessage({ id: 'none' }) : (
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
  function renderLevel({ text, record }) {
    return <span style={{ color: !record.get('accessLevelSyncFlag') ? '#EF4E42' : '' }}>{text}</span>;
  }
  function renderDate({ text, record }) {
    return <span style={{ color: !record.get('expiresAtSyncFlag') ? '#EF4E42' : '' }}>{text || '-'}</span>;
  }

  return (
    <Page
      className="c7n-infra-code-management-table"
      service={[
        'choerodon.code.project.infra.code-lib-management.ps.project-owner',
      ]}
    >
      <div >
        <Row className="c7n-infra-code-management-ps-audit-tip-text">
          <Col span={8}>
            <Icon type="date_range" style={{ marginRight: '.03rem', marginBottom: '.04rem' }} />
            <span>{formatMessage({ id: `${intlPrefix}.model.executionDate` })}<span className="c7n-infra-code-management-ps-audit-tip-text-date">{renderTime()}</span></span>
          </Col>
          <Col span={8}>
            <Icon type="compare" style={{ marginRight: '.03rem' }} />
            <span>{formatMessage({ id: `${intlPrefix}.model.diffCount` })}<span className="c7n-infra-code-management-ps-audit-tip-text-number">{psAuditDs.totalCount || 0}</span></span>
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
        <Column renderer={renderAction} width={70} />
        <Column name="loginName" renderer={renderLoginName} />
        <Column name="repositoryName" width={180} />
        <Column name="accessLevel" renderer={renderLevel} />
        <Column name="expiresAt" renderer={renderDate} />
        <Column name="glAccessLevel" renderer={renderLevel} />
        <Column name="glExpiresAt" renderer={renderDate} />
      </Table>
    </Page>
  );
});

export default PsSet;
