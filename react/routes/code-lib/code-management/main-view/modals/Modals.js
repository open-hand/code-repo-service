import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import { Modal } from 'choerodon-ui/pro';
import { Header, Choerodon } from '@choerodon/boot';
import HeaderButtons from '@/components/header-buttons';
import AddMember from './add-member';
import AddOutsideMember from './add-outside-member';
// import ImportMember from './import-member';
import AddBranch from './add-branch';
import AddTag from './add-tag';
import PsApply from './ps-apply';
import { usPsManagerStore } from '../stores';
import ExportAuthority from './export-authority';

const modalKey = Modal.key();
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
  const { type } = props;
  const [exportModalVisible, setExportModalVisible] = useState(false);

  async function fetchExecutionDate() {
    await overStores.fetchExecutionDate(organizationId, projectId)
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
  // function refreshApproval() {
  //   psApprovalDs.query();
  // }

  function openAdd() {
    Modal.open({
      key: modalKey,
      style: modalStyle,
      drawer: true,
      title: formatMessage({ id: 'infra.add.member' }),
      children: <AddMember
        refresh={refresh}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        branchServiceDs={branchServiceDs}
      />,
      okText: formatMessage({ id: 'add' }),
    });
  }
  function openAddOutside() {
    Modal.open({
      key: modalKey,
      style: modalStyle,
      drawer: true,
      title: formatMessage({ id: 'infra.add.outsideMember' }),
      children: <AddOutsideMember
        refresh={refresh}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
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
        // refresh={refreshApproval}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        branchServiceDs={branchServiceDs}
      />,
      okText: formatMessage({ id: 'add' }),
    });
  }
  // function openImport() {
  //   Modal.open({
  //     children: <ImportMember onOk={refresh} />,
  //     key: modalKey,
  //     drawer: true,
  //     style: { width: 380 },
  //     fullScreen: true,
  //     destroyOnClose: true,
  //     className: 'base-site-user-sider',
  //     okText: '返回',
  //     okCancel: false,
  //     title: '导入成员',
  //   });
  // }
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

  function getButtons() {
    const buttonData = [{
      name: formatMessage({ id: 'refresh' }),
      icon: 'refresh',
      handler: refresh,
      display: true,
      group: 1,
    }];
    switch (type) {
      case 'psSet':
        buttonData.unshift(
          {
            name: formatMessage({ id: 'infra.add.member' }),
            icon: 'person_add',
            handler: openAdd,
            display: true,
            group: 1,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
          },
          {
            name: formatMessage({ id: 'infra.add.outsideMember' }),
            icon: 'person_add',
            handler: openAddOutside,
            display: true,
            group: 1,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
          },
          // {
          //   name: formatMessage({ id: 'infra.button.import-user' }),
          //   icon: 'archive',
          //   handler: openImport,
          //   display: true,
          //   group: 1,
          //   permissions: permissions,
          // },
          {
            name: formatMessage({ id: 'infra.operate.export.permission' }),
            icon: 'get_app',
            handler: () => setExportModalVisible(true),
            display: true,
            group: 1,
            permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-member'],
          },
        );
        break;
      case 'applyView':
        buttonData.unshift({
          name: formatMessage({ id: 'infra.permission.request' }),
          icon: 'person_add',
          handler: openRequest,
          display: true,
          group: 1,
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
          group: 1,
          permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
        }, {
          name: formatMessage({ id: 'infra.add.tag' }),
          icon: 'playlist_add',
          handler: openTag,
          display: true,
          disabled: !branchAppId,
          group: 1,
          permissions: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
        });
        break;
      default:
    }
    return buttonData;
  }

  return (
    <Header>
      <HeaderButtons items={getButtons()} />
      <ExportAuthority
        formatMessage={formatMessage}
        exportModalVisible={exportModalVisible}
        setExportModalVisible={setExportModalVisible}
        psSetDs={psSetDs}
      />
    </Header>
  );
});

export default EnvModals;
