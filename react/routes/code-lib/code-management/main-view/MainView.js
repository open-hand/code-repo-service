/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useCallback, useMemo } from 'react';
import { PageWrap, PageTab, Page, Content } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import Tips from '@/components/new-tips';
import { usPsManagerStore } from './stores';
import PsSet from './ps-set';
import ApplyView from './apply-view';
import PsBranch from './ps-branch';
import PsOverView from './ps-view';
import OperationLog from './operation-log';
import PsApproval from './ps-approval';
import PsAudit from './ps-audit';
import SecurityAudit from './security-audit';
import './index.less';

const MainView = observer(() => {
  const {
    intlPrefix,
    intl: { formatMessage },
    hasPermission,
    hasMemberPermission,
  } = usPsManagerStore();

  const renderPageWrap = useMemo(() => {
    let pageWrap = (
      <PageTab
        title={formatMessage({ id: `${intlPrefix}.psSet` })}
        tabKey="psSet"
        route="/rducm/code-lib-management/assign"
        component={PsSet}
        alwaysShow
      />
    );
    if (hasMemberPermission && !hasPermission) {
      pageWrap = [
        <PageTab
          title={formatMessage({ id: `${intlPrefix}.psSet` })}
          tabKey="psSet"
          route="/rducm/code-lib-management/assign"
          component={PsSet}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.applyView' })}
          tabKey="applyView"
          route="/rducm/code-lib-management/apply"
          component={ApplyView}
            // alwaysShow={hasMemberPermission}
          alwaysShow
        />,
      ];
    } else if (hasPermission && !hasMemberPermission) {
      pageWrap = [
        <PageTab
          title={formatMessage({ id: `${intlPrefix}.psSet` })}
          tabKey="psSet"
          route="/rducm/code-lib-management/assign"
          component={PsSet}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.psApproval' })}
          tabKey="psApproval"
          route="/rducm/code-lib-management/approve"
          component={PsApproval}
          alwaysShow
        />,
        <PageTab
          title={<Tips
            helpText={formatMessage({ id: 'infra.codeManage.ps.message.psAudit.tips' })}
            title={formatMessage({ id: 'infra.codeManage.ps.message.psAudit' })}
          />}
          tabKey="psAudit"
          route="/rducm/code-lib-management/audit"
          component={PsAudit}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.securityAudit' })}
          tabKey="securityAudit"
          route="/rducm/code-lib-management/security"
          component={SecurityAudit}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.psBranch' })}
          tabKey="psBranch"
          route="/rducm/code-lib-management/branch"
          component={PsBranch}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.operationLog' })}
          tabKey="operationLog"
          route="/rducm/code-lib-management/log"
          component={OperationLog}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: `${intlPrefix}.psOverView` })}
          tabKey="psOverView"
          route="/rducm/code-lib-management/view"
          component={PsOverView}
          alwaysShow
        />,
      ];
    } else if (hasPermission && hasMemberPermission) {
      pageWrap = [
        <PageTab
          title={formatMessage({ id: `${intlPrefix}.psSet` })}
          tabKey="psSet"
          route="/rducm/code-lib-management/assign"
          component={PsSet}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.applyView' })}
          tabKey="applyView"
          route="/rducm/code-lib-management/apply"
          component={ApplyView}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.psApproval' })}
          tabKey="psApproval"
          route="/rducm/code-lib-management/approve"
          component={PsApproval}
            // alwaysShow={hasPermission}
          alwaysShow
        />,
        <PageTab
          title={<Tips
            helpText={formatMessage({ id: 'infra.codeManage.ps.message.psAudit.tips' })}
            title={formatMessage({ id: 'infra.codeManage.ps.message.psAudit' })}
          />}
          tabKey="psAudit"
          route="/rducm/code-lib-management/audit"
          component={PsAudit}
            // alwaysShow={hasPermission}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.securityAudit' })}
          tabKey="securityAudit"
          route="/rducm/code-lib-management/security"
          component={SecurityAudit}
            // alwaysShow={hasPermission}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.psBranch' })}
          tabKey="psBranch"
          route="/rducm/code-lib-management/branch"
          component={PsBranch}
            // alwaysShow={hasPermission}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: 'infra.codeManage.ps.message.operationLog' })}
          tabKey="operationLog"
          route="/rducm/code-lib-management/log"
          component={OperationLog}
            // alwaysShow={hasPermission}
          alwaysShow
        />,
        <PageTab
          title={formatMessage({ id: `${intlPrefix}.psOverView` })}
          tabKey="psOverView"
          route="/rducm/code-lib-management/view"
          component={PsOverView}
            // alwaysShow={hasPermission}
          alwaysShow
        />,
      ];
    }
    return (
      <PageWrap noHeader={[]}>
        {pageWrap}
      </PageWrap>
    );
  }, [hasPermission, hasMemberPermission]);

  return (
    <Page className="c7n-infra-code-management" >
      <div className="c7n-infra-code-management-tab-list">
        {/* <PageWrap noHeader={[]} > */}
        {renderPageWrap}
        {/* </PageWrap> */}
      </div>
    </Page>);
});

export default MainView;
