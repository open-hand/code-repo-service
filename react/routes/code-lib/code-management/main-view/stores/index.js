/* eslint-disable */
import React, { createContext, useContext, useMemo, useEffect, useState } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { useCheckPermission } from '@/utils/index';
import queryString from 'querystring';

import PsSetDataSet from './PsSetDataSet';
import BranchDataSet from './BranchDataSet';
import TagDataSet from './TagDataSet';
import PsOverViewDataSet from './PsOverViewDataSet';
// import usePermissionStore from '../modals/stores/useStore';
// import PsManagerDataSet from './PsManagerDataSet';
import BranchServiceDs from './BranchServiceDataSet';
import ListViewDataSet from './ListViewDataSet';
import useStore from './useStore';
import PsApprovalDS from './PsApprovalDS';
import PsAuditDS from './PsAuditDS';
import SecurityAuditDS from './SecurityAuditDS';
import ApplyViewDS from './ApplyViewDS';
import { useManagementStore } from '../../stores';

const Store = createContext();

export function usPsManagerStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    AppState: { currentMenuType: { projectId, organizationId } },
    intl: { formatMessage },
    children,
  } = props;
  const { location } = useManagementStore();
  const { appServiceIds } = queryString.parse(location.search);

  const [appId, setApp] = useState(undefined);
  const [branchAppId, setBranchApp] = useState(undefined);

  const hasMemberPermission = useMemo(() => useCheckPermission(['choerodon.code.project.infra.code-lib-management.ps.project-member'], true), []);
  const hasPermission = useMemo(() => useCheckPermission(['choerodon.code.project.infra.code-lib-management.ps.project-owner'], true), []);

  const intlPrefix = 'infra';
  const branchServiceDs = useMemo(() => new DataSet(BranchServiceDs({
    formatMessage, organizationId, projectId, setBranchApp,
  })), [projectId, organizationId]); // 分支/标签的应用服务下拉框数据
  const psSetDs = useMemo(() => new DataSet(PsSetDataSet(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs, appServiceIds)), [formatMessage, projectId, branchServiceDs, appServiceIds]);
  const branchDs = useMemo(() => new DataSet(BranchDataSet(formatMessage, projectId, branchAppId)), [formatMessage, projectId, branchAppId]);
  const tagDs = useMemo(() => new DataSet(TagDataSet(formatMessage, projectId, branchAppId)), [formatMessage, projectId, branchAppId]);
  const psOverViewDs = useMemo(() => new DataSet(PsOverViewDataSet(formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);

  const listViewDs = useMemo(() => new DataSet(ListViewDataSet(formatMessage, organizationId, projectId)), [formatMessage, projectId]); // 操作日志
  const overStores = useStore();

  const psApprovalDs = useMemo(() => new DataSet(PsApprovalDS(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);
  const [executionDate, setExecutionDate] = useState(undefined);
  const psAuditDs = useMemo(() => new DataSet(PsAuditDS(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);
  const securityAuditDs = useMemo(() => new DataSet(SecurityAuditDS(intlPrefix, formatMessage, organizationId, projectId)), [formatMessage, projectId]);
  const applyViewDs = useMemo(() => new DataSet(ApplyViewDS(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);


  const value = {
    ...props,
    branchServiceDs,
    psSetDs,
    branchDs,
    tagDs,
    psOverViewDs,
    params: {
      projectId,
    },
    prefixCls: 'code-management',
    intlPrefix,
    appId,
    setApp,
    branchAppId,
    setBranchApp,
    listViewDs,
    overStores,
    psApprovalDs,
    organizationId,
    projectId,
    executionDate,
    setExecutionDate,
    psAuditDs,
    securityAuditDs,
    hasPermission,
    hasMemberPermission,
    applyViewDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
