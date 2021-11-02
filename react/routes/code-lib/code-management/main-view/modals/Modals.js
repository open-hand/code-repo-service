/* eslint-disable react/jsx-no-bind */
/* eslint-disable max-len */
import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import { message } from 'choerodon-ui';
import { Modal, ModalProvider } from 'choerodon-ui/pro';
import { Header, Choerodon, axios, HeaderButtons } from '@choerodon/boot';
import BatchApprove from './batch-approval';
import AddMember from './add-member';
import AddBranch from './add-branch';
import AddTag from './add-tag';
import PsApply from './ps-apply';
import { usPsManagerStore } from '../stores';
import { useManagementStore } from '../../stores';
import ExportAuthority from './export-authority';
import Apis from '../../apis';
import { FixModal, AuditModal } from './ps-audit';


const modalKey = Modal.key();
const SyncKey = Modal.key();
const deleteKey = Modal.key();
const modalStyle = {
  width: 740,
};

const EnvModals = observer((props) => {
  const {
    intlPrefix,
    intl: { formatMessage },
    prefixCls,
    psSetDs,
    branchDs,
    tagDs,
    psOverViewDs,
    psApprovalDs,
    branchAppId,
    setExecutionDate,
    psAuditDs,
    securityAuditDs,
    overStores,
    organizationId,
    projectId,
    branchServiceDs,
    applyViewDs,
  } = usPsManagerStore();

  const {
    hasPermission,
  } = useManagementStore();

  const { type } = props;
  const [exportModalVisible, setExportModalVisible] = useState(false);

  async function fetchExecutionDate() {
    try {
      const res = Apis.fetchExecutionDate(organizationId, projectId);
      if (res.failed) {
        Choerodon.prompt(res.message);
        return false;
      }
      const dataStr = res.auditEndDate
        ? moment(res.auditEndDate).format('YYYY-MM-DD HH:mm:ss')
        : undefined;
      setExecutionDate(dataStr);
      return true;
    } catch (error) {
      Choerodon.handleResponseError(error);
      return false;
    }
  }

  function refresh() {
    switch (type) {
      case 'psSet':
        psSetDs.query();
        break;
      case 'psBranch':
        branchDs.query();
        tagDs.query();
        break;
      case 'psView':
        psOverViewDs.query();
        break;
      case 'psApproval':
        psApprovalDs.query();
        break;
      case 'psAudit':
        psAuditDs.query();
        fetchExecutionDate();
        break;
      case 'securityAudit':
        securityAuditDs.query();
        break;
      case 'applyView':
        applyViewDs.query();
        break;
      default:
        psSetDs.query();
    }
  }
  function refreshBranch() {
    branchDs.query();
  }
  function refreshTag() {
    tagDs.query();
  }

  function openAdd(openType) {
    let strId;
    if (openType === 'project') {
      strId = 'infra.add.member';
    } else {
      strId = 'infra.add.outsideMember';
    }
    Modal.open({
      key: modalKey,
      style: modalStyle,
      drawer: true,
      title: formatMessage({ id: `${strId}` }),
      closeOnLocationChange: true,
      children: <AddMember
        openType={openType}
        refresh={refresh}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        currentBranchAppId={branchAppId}
        branchServiceDs={branchServiceDs}
      />,
      okText: formatMessage({ id: 'add' }),
    });
  }
  function openRequest() {
    Modal.open({
      key: modalKey,
      style: { width: '3.8rem' },
      drawer: true,
      title: formatMessage({ id: 'infra.permission.request' }),
      children: <PsApply
        refresh={refresh}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        currentBranchAppId={branchAppId}
        branchServiceDs={branchServiceDs}
      />,
      okText: formatMessage({ id: 'add' }),
    });
  }
  function openBranch() {
    Modal.open({
      title: formatMessage({ id: 'infra.add.branch' }),
      children: <AddBranch
        refresh={refreshBranch}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        branchAppId={branchAppId}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: 'base-lang-sider',
      okText: formatMessage({ id: 'add' }),
    });
  }
  function openTag() {
    Modal.open({
      title: formatMessage({ id: 'infra.add.tag' }),
      children: <AddTag
        refresh={refreshTag}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        branchAppId={branchAppId}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: 'base-lang-sider',
      okText: formatMessage({ id: 'add' }),
    });
  }

  function openDeleteModal() {
    Modal.open({
      key: deleteKey,
      title: formatMessage({ id: 'infra.button.batch.delete' }),
      children: '确认要删除选中的用户对应的代码库权限吗？',
      okText: formatMessage({ id: 'delete' }),
      onOk: handleDelete,
    });
  }

  async function handleDelete() {
    const deleteData = psSetDs.selected.map(item => item.get('id'));
    await axios.delete(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/batch-remove`, { params: { memberIds: deleteData.join(',') } })
      .then((res) => {
        if (res.failed) {
          message.error(res.message);
        } else {
          message.success(formatMessage({ id: 'infra.view.message.deleteSuccess' }));
          psSetDs.query();
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
      });
  }

  function handleSyncOpenModal() {
    Modal.open({
      title: formatMessage({ id: 'infra.button.batch.sync' }),
      children: '确认要全部将【未同步】状态用户的代码权限与GitLab仓库内用户的权限进行同步吗？',
      onOk: handleSync,
      key: SyncKey,
    });
  }

  async function handleSync() {
    try {
      const res = await axios.get(`/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/members/all/sync`);
      if (res && res.failed) {
        message.error('用户同步失败，请检查后重试');
        return true;
      }
      psSetDs.query();
      return true;
    } catch (error) {
      Choerodon.handleResponseError(error);
      return false;
    }
  }

  /**
   * 批量审批
   */
  function handlerBatchApprove(ds, func) {
    Modal.open({
      okText: '保存',
      key: Modal.key(),
      title: '权限批量审批',
      drawer: true,
      children: <BatchApprove selects={ds.selected} func={func} />,
      style: {
        width: 380,
      },
    });
  }

  async function handlerBatchAudit() {
    try {
      const res = await Apis.bacthAuidt(organizationId, projectId);
      if (res && res.failed) {
        message.error('手动审计失败');
        return true;
      }
      psAuditDs.query();
      return true;
    } catch (error) {
      throw new Error(error);
    }
  }

  async function handlerBatchAuditFix() {
    try {
      const res = await Apis.bacthfix(organizationId, projectId, psAuditDs.selected.map(item => item.get('id')));
      if (res && res.failed) {
        message.error('批量修复失败');
        return true;
      }
      psAuditDs.query();
      return true;
    } catch (error) {
      throw new Error(error);
    }
  }

  function handleBatchFixModalOpen() {
    Modal.open({
      key: Modal.key(),
      title: '批量修复',
      children: <FixModal onOk={handlerBatchAuditFix} organizationId={organizationId} projectId={projectId} />,
    });
  }

  function handleBatchAuditModalOpen() {
    Modal.open({
      key: Modal.key(),
      title: '手动审计',
      children: <AuditModal onOk={handlerBatchAudit} organizationId={organizationId} projectId={projectId} />,
    });
  }

  function getButtons() {
    const buttonData = [{
      name: formatMessage({ id: 'refresh' }),
      icon: 'refresh',
      iconOnly: true,
      handler: refresh,
      color: 'default',
      display: true,
    }];
    const disabledPsApproval = !(psApprovalDs.selected && psApprovalDs.selected.length > 0);
    const disabledPsAudit = !(psAuditDs.selected && psAuditDs.selected.length > 0);
    switch (type) {
      case 'psAudit':
        buttonData.unshift({
          name: '手动审计',
          icon: 'playlist_add_check',
          handler: handleBatchAuditModalOpen,
        }, {
          name: '批量修复',
          icon: 'person_add-o',
          handler: handleBatchFixModalOpen,
          display: true,
          disabled: disabledPsAudit,
          tooltipsConfig: {
            placement: 'bottom',
            title: disabledPsAudit ? '请在下方列表中勾选用户' : '',
          },
        });
        break;
      case 'psApproval':
        buttonData.unshift({
          name: '批量审批',
          icon: 'playlist_add_check',
          handler: () => handlerBatchApprove(psApprovalDs, refresh),
          display: true,
          disabled: disabledPsApproval,
          tooltipsConfig: {
            placement: 'bottom',
            title: disabledPsApproval ? '请在下方列表中选择【待审批】状态的申请' : '',
          },
        });
        break;
      case 'psSet':
        buttonData.unshift(
          {
            name: formatMessage({ id: 'infra.add.member' }),
            icon: 'person_add-o',
            handler: () => { openAdd('project'); },
            display: true,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
          },
          {
            name: formatMessage({ id: 'infra.add.outsideMember' }),
            icon: 'person_add-o',
            handler: () => { openAdd('nonProject'); },
            display: true,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
          },
          {
            name: formatMessage({ id: 'infra.operate.export.permission' }),
            icon: 'get_app-o',
            handler: () => setExportModalVisible(true),
            display: true,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
          },
          {
            name: formatMessage({ id: 'infra.button.batch.sync' }),
            icon: 'sync',
            handler: handleSyncOpenModal,
            display: true,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
          },
          {
            name: formatMessage({ id: 'infra.button.batch.delete' }),
            icon: 'delete',
            handler: openDeleteModal,
            display: true,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
            disabled: psSetDs.selected.length === 0,
            tooltipsConfig: {
              title: psSetDs.selected.length === 0 ? '请在列表中勾选需要删除的用户' : '',
            },
          },
        );
        break;
      case 'applyView':
        buttonData.unshift({
          name: formatMessage({ id: 'infra.permission.request' }),
          icon: 'person_add-o',
          handler: openRequest,
          display: true,
          permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-member'],
        });
        break;
      case 'psBranch':
        buttonData.unshift({
          name: formatMessage({ id: 'infra.add.branch' }),
          icon: 'playlist_add',
          handler: openBranch,
          display: true,
          disabled: !branchAppId,
          permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
        }, {
          name: formatMessage({ id: 'infra.add.tag' }),
          icon: 'playlist_add',
          handler: openTag,
          display: true,
          disabled: !branchAppId,
          permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
        });
        break;
      default:
    }
    return buttonData;
  }

  return (
    <ModalProvider location={window.location}>
      <Header>
        <HeaderButtons items={getButtons()} />
        {
          hasPermission && <ExportAuthority
            formatMessage={formatMessage}
            exportModalVisible={exportModalVisible}
            setExportModalVisible={setExportModalVisible}
            psSetDs={psSetDs}
          />
        }
      </Header>
    </ModalProvider>
  );
});

export default EnvModals;
