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
import UserAvatar from '@/components/user-avatar';
import { usPsManagerStore } from '../stores';
import Sider from '../modals/ps-approval';

const { Column } = Table;
const intlPrefix = 'infra.codeManage.ps';

const modalKey = Modal.key();
const PsApproval = observer(() => {
  const {
    // intlPrefix,
    // prefixCls,
    intl: { formatMessage },
    psApprovalDs,
    psSetDs,
    appId,
    overStores,
    organizationId,
    projectId,
  } = usPsManagerStore();

  function refresh() {
    psApprovalDs.query();
  }

  useEffect(() => {
    refresh();
  }, [appId]);

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  function handleOpenModal(record) {
    psApprovalDs.current = record;
    const isApproval = record.getPristineValue('approvalState') === 'PENDING';
    Modal.open({
      key: modalKey,
      drawer: true,
      title: isApproval ? formatMessage({ id: `${intlPrefix}.message.approveDrawerTitle` }, { name: record.get('applicantUser').realName }) : formatMessage({ id: `${intlPrefix}.message.approveDetail` }, { name: record.get('applicantUser').realName }),
      style: { width: 380 },
      className: 'code-lib-ps-approval-sider',
      children: (
        <Sider
          formatMessage={formatMessage}
          record={record}
          psApprovalDs={psApprovalDs}
          overStores={overStores}
          organizationId={organizationId}
          projectId={projectId}
          psSetDs={psSetDs}
        />
      ),
      okCancel: isApproval,
      fullScreen: true,
      okText: isApproval ? formatMessage({ id: 'commit' }) : formatMessage({ id: 'close' }),
    });
  }

  function renderName({ record }) {
    const avatar = (
      <UserAvatar
        user={{
          id: record.get('applicantUser').userId,
          loginName: record.get('applicantUser').loginName,
          realName: record.get('applicantUser').realName,
          imageUrl: record.get('applicantUser').imageUrl,
          email: record.get('applicantUser').email,
        }}
        hiddenText
      />
    );

    return (
      <div style={{ display: 'inline-flex' }}>
        {record.get('approvalState') === 'PENDING' ? (
          <React.Fragment>
            {avatar}
            <span className="c7n-infra-code-management-user-head-name">{record.get('applicantUser').realName}</span>
          </React.Fragment>
        ) :
          (
            <React.Fragment>
              {avatar}
              <span className="c7n-infra-code-management-table-name" onClick={() => handleOpenModal(record)}>{record.get('applicantUser').realName}</span>
            </React.Fragment>
          )}
      </div>
    );
  }

  function renderAction({ record }) {
    const actionDatas = [];
    if (record.get('approvalState') === 'PENDING') {
      actionDatas.push({
        service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
        text: formatMessage({ id: 'approve' }),
        action: () => handleOpenModal(record),
      });
    } else {
      return;
    }
    return (
      <Action data={actionDatas} />
    );
  }

  function renderStatus(record) {
    const res = record.value.toLowerCase();
    const approvalMessage = record.record.get('approvalMessage');
    return (
      <React.Fragment>
        <StatusTag colorCode={record.value.toUpperCase()} name={formatMessage({ id: `infra.approval.${res}` })} />
        {
          approvalMessage && <Tooltip title={approvalMessage}>
            <Icon style={{
            marginLeft: '5px',
            color: '#F76776',
            }} />
          </Tooltip>
        }
      </React.Fragment>
    );
  }

  return (
    <Page
      service={[
        'choerodon.code.project.infra.code-lib-management.ps.project-owner',
      ]}
      className="c7n-infra-code-management-table"
    >
      <Table
        dataSet={psApprovalDs}
        filter={handleTableFilter}
        queryBar="bar"
        queryFieldsLimit={3}
      >
        <Column name="applicantUser" renderer={renderName} width={200} />
        <Column renderer={renderAction} width={70} />
        <Column name="approvalState" renderer={renderStatus} />
        <Column name="repositoryName" />
        <Column name="applicantType" />
        <Column name="accessLevel" />
        <Column name="applicantDate" />
      </Table>
    </Page>
  );
});

export default PsApproval;
