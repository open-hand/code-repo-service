/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect } from 'react';
import { Page, Action } from '@choerodon/boot';
import { Table, Modal, Icon, Tooltip } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { StatusTag } from '@choerodon/components';
import { usPsManagerStore } from '../stores';
import ApplyDetail from '../modals/apply-detail';

const { Column } = Table;
const modalKey = Modal.key();
const ApplyView = observer(() => {
  const {
    intl: { formatMessage },
    applyViewDs,
    appId,
  } = usPsManagerStore();

  function refresh() {
    applyViewDs.query();
  }

  useEffect(() => {
    refresh();
  }, [appId]);

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function renderStatus(record) {
    const res = record.value.toLowerCase();
    const error = record.record.get('approvalMessage');
    return (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <StatusTag colorCode={record.value.toUpperCase()} name={formatMessage({ id: `infra.approval.${res}` })} style={{ lineHeight: '16px', width: '42px' }} />
        {error && <Tooltip title={error}>
          <Icon
            style={{
            color: 'rgb(247, 103, 118)',
            marginLeft: '3px',
            }}
            type="info"
          />
        </Tooltip>}
      </div>
    );
  }

  function handleOpenModal(record) {
    applyViewDs.current = record;
    const isApproval = record.getPristineValue('approvalState') === 'PENDING';
    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: 'infra.codeManage.ps.message.apply.detail' }),
      style: { width: 380 },
      className: 'code-lib-ps-approval-sider',
      children: (
        <ApplyDetail
          formatMessage={formatMessage}
          record={record}
          psApprovalDs={applyViewDs}
        />
      ),
      okCancel: isApproval,
      fullScreen: true,
      okText: isApproval ? formatMessage({ id: 'commit' }) : formatMessage({ id: 'close' }),
    });
  }

  function renderAction({ record }) {
    const actionDatas = [];
    if (record.get('approvalState') !== 'PENDING') {
      actionDatas.push({
        service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
        text: formatMessage({ id: 'view.detail', defaultMessage: '查看详情' }),
        action: () => handleOpenModal(record),
      });
    } else {
      return;
    }
    return (
      <Action data={actionDatas} />
    );
  }

  return (
    <Page
      service={[
        'choerodon.code.project.infra.code-lib-management.ps.project-member',
      ]}
      className="c7n-infra-code-management-table"
    >
      <Table
        dataSet={applyViewDs}
        filter={handleTableFilter}
        queryBar="bar"
        queryFieldsLimit={3}
      >
        <Column name="repositoryName" width={250} />
        <Column renderer={renderAction} width={70} />
        <Column name="applicantType" />
        <Column name="accessLevel" />
        <Column name="approvalState" renderer={renderStatus} />
        <Column name="applicantDate" />
        {/* <Column name="approvalDate" /> */}
      </Table>
    </Page>
  );
});

export default ApplyView;
