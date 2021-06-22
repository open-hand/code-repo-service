/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useCallback, useEffect, useState } from 'react';
import { PageWrap, PageTab, Page } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { checkPermission } from '@/utils';
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
import PsAll from './ps-all';
import './index.less';

const MainView = observer(() => {
  const {
    intlPrefix,
    intl: { formatMessage },
    AppState: { currentMenuType: { projectId, type } },
    overStores,
  } = usPsManagerStore();

  const [hasMemberPermission, sethasMemberPermission] = useState(false);
  const [hasPermission, sethasPermission] = useState(false);

  const init = useCallback(async () => {
    const hasMemberPermission1 = await checkPermission({ projectId, code: ['choerodon.code.project.infra.code-lib-management.ps.project-member'], resourceType: type });
    const hasPermission1 = await checkPermission({ projectId, code: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'], resourceType: type });
    sethasMemberPermission(hasMemberPermission1);
    sethasPermission(hasPermission1);
  }, [projectId, type]);

  useEffect(() => {
    init();
  }, []);

  const renderPageWrap = () => {
    const hasMemberPermission = overStores.getHasMemberPermission;
    const hasPermission = overStores.getHasPermission;
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
    }
    return pageWrap;
  };

  return (
    <Page className="c7n-infra-code-management" >
      <div className="c7n-infra-code-management-tab-list">
        <PageWrap noHeader={[]}>
          <PageTab
            title={formatMessage({ id: `${intlPrefix}.permission` })}
            tabKey="permission"
            route="/rducm/code-lib-management/permission"
            component={PsAll}
            alwaysShow
          />
          {renderPageWrap()}
        </PageWrap>
      </div>
    </Page>);
});

export default MainView;
