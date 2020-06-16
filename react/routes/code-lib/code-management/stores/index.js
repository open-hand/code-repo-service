import React, { createContext, useContext, useMemo } from 'react';
// import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
// import AppServiceDS from './AppServiceDS';
// import SelectAppDS from './SelectAppDS';
import useStore from './useStore';

const Store = createContext();

export function useManagementStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const { children } = props;

  const tabs = useMemo(() => ({
    PS_ASSIGN_TAB: 'psAssign',
    PS_APPROVAL_TAB: 'psApproval',
    PS_AUDIT_TAB: 'psAudit',
    SECURITY_AUDIT_TAB: 'securityAudit',
    BRANCH_TAG_TAB: 'branchTag',
    LOG_TAB: 'psLog',
    PS_OVERVIEW_TAB: 'psOverview',
  }), []);

  // const appServiceDs = useMemo(() => new DataSet(AppServiceDS({ organizationId, projectId })), [projectId]);
  // const selectAppDs = useMemo(() => new DataSet(SelectAppDS()), [projectId]);

  const managementStore = useStore(tabs);

  const value = {
    ...props,
    prefixCls: 'c7ncd-cluster',
    // prefixCls: 'infra-code-lib',
    intlPrefix: 'infra.code.lib',
    permissions: [
      'choerodon.code.project.infra.code-lib-management.ps.project-member',
      'choerodon.code.project.infra.code-lib-management.ps.project-owner',
    ],
    tabs,
    // appServiceDs,
    managementStore,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
