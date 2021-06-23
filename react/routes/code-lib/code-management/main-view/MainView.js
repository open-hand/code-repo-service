/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useCallback, useEffect, useState } from 'react';
import { PageWrap, PageTab, Page } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { usPsManagerStore } from './stores';
import PsSet from './ps-set';
import PsBranch from './ps-branch';
import PsOverView from './ps-view';
import OperationLog from './operation-log';
import SecurityAudit from './security-audit';
import PsAll from './ps-all';
import './index.less';

const MainView = observer(() => {
  const {
    intlPrefix,
    intl: { formatMessage },
    hasPermission,
  } = usPsManagerStore();

  const renderPageWrap = useCallback(() => {
    let pageWrap = (
      <PageTab
        title={formatMessage({ id: `${intlPrefix}.psSet` })}
        tabKey="psSet"
        route="/rducm/code-lib-management/assign"
        component={PsSet}
        alwaysShow
      />
    );
    if (hasPermission) {
      pageWrap = [
        <PageTab
          title={formatMessage({ id: `${intlPrefix}.permission` })}
          tabKey="permission"
          route="/rducm/code-lib-management/permission"
          component={PsAll}
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
  }, [hasPermission]);

  return (
    <Page className="c7n-infra-code-management" >
      <div className="c7n-infra-code-management-tab-list">
        <PageWrap noHeader={[]}>
          {renderPageWrap()}
        </PageWrap>
      </div>
    </Page>);
});

export default MainView;
