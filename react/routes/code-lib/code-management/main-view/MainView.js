/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React from 'react';
import { PageWrap, PageTab, Page } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
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
        <PageWrap noHeader={[]} cache>
          <PageTab
            title={formatMessage({ id: `${intlPrefix}.psSet` })}
            tabKey="psSet"
            component={PsSet}
            alwaysShow
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.applyView' })}
            tabKey="psApproval"
            component={ApplyView}
            alwaysShow={hasMemberPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.psApproval' })}
            tabKey="psApproval"
            component={PsApproval}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.psAudit' })}
            tabKey="psAudit"
            component={PsAudit}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.securityAudit' })}
            tabKey="securityAudit"
            component={SecurityAudit}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.psBranch' })}
            tabKey="psBranch"
            component={PsBranch}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: 'infra.codeManage.ps.message.operationLog' })}
            tabKey="operationLog"
            component={OperationLog}
            alwaysShow={hasPermission}
          />
          <PageTab
            title={formatMessage({ id: `${intlPrefix}.psOverView` })}
            tabKey="psOverView"
            component={PsOverView}
            alwaysShow={hasPermission}
          />
        </PageWrap>
      </div>
    </Page>);
});

export default MainView;
