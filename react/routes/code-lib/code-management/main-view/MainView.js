/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React from 'react';
import { PageWrap, PageTab, Page } from '@choerodon/boot';
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

  return (
    <Page className="c7n-infra-code-management" >
      <div className="c7n-infra-code-management-tab-list">
        <PageWrap noHeader={[]} >
          <PageTab
            title={formatMessage({ id: `${intlPrefix}.psSet` })}
            tabKey="psSet"
            route="/rducm/code-lib-management/assign"
            component={PsSet}
            alwaysShow
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.applyView' })}
            tabKey="applyView"
            route="/rducm/code-lib-management/apply"
            component={ApplyView}
            alwaysShow={hasMemberPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.psApproval' })}
            tabKey="psApproval"
            route="/rducm/code-lib-management/approve"
            component={PsApproval}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={<Tips
              helpText={formatMessage({ id: 'infra.codeManage.ps.message.psAudit.tips' })}
              title={formatMessage({ id: 'infra.codeManage.ps.message.psAudit' })}
            />}
            tabKey="psAudit"
            route="/rducm/code-lib-management/audit"
            component={PsAudit}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.securityAudit' })}
            tabKey="securityAudit"
            route="/rducm/code-lib-management/security"
            component={SecurityAudit}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.psBranch' })}
            tabKey="psBranch"
            route="/rducm/code-lib-management/branch"
            component={PsBranch}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.operationLog' })}
            tabKey="operationLog"
            route="/rducm/code-lib-management/log"
            component={OperationLog}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: `${intlPrefix}.psOverView` })}
            tabKey="psOverView"
            route="/rducm/code-lib-management/view"
            component={PsOverView}
            alwaysShow={hasPermission}
          />
        </PageWrap>
      </div>
    </Page>);
});

export default MainView;
