import React, { createContext, useEffect, useState, useContext, useMemo, useCallback } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { checkPermission } from '@/utils';
import useStore from './useStore';

const Store = createContext();

export function useManagementStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: { currentMenuType: { projectId, type }, getUserInfo },
  } = props;

  const [hasMemberPermission, sethasMemberPermission] = useState(false);
  const [hasPermission, sethasPermission] = useState(false);

  const tabs = useMemo(() => ({
    PS_ASSIGN_TAB: 'psAssign',
    PS_APPROVAL_TAB: 'psApproval',
    PS_AUDIT_TAB: 'psAudit',
    SECURITY_AUDIT_TAB: 'securityAudit',
    BRANCH_TAG_TAB: 'branchTag',
    LOG_TAB: 'psLog',
    PS_OVERVIEW_TAB: 'psOverview',
  }), []);

  const managementStore = useStore(tabs);

  const init = useCallback(async () => {
    const codePermissions = localStorage.getItem(`codePermissions-${JSON.stringify(getUserInfo)}-${projectId}`);
    if (codePermissions) {
      try {
        const {
          localMemberPermission,
          localOwnerPermission,
        } = JSON.parse(codePermissions);
        sethasMemberPermission(localMemberPermission);
        sethasPermission(localOwnerPermission);
      } catch (error) {
        throw new Error('parse the codePermissions in localstorage error');
      }
    } else {
      const hasMemberPermission1 = await checkPermission({ projectId, code: ['choerodon.code.project.infra.code-lib-management.ps.project-member'], resourceType: type });
      const hasPermission1 = await checkPermission({ projectId, code: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'], resourceType: type });
      sethasMemberPermission(hasMemberPermission1);
      sethasPermission(hasPermission1);
      localStorage.setItem(`codePermissions-${JSON.stringify(getUserInfo)}-${projectId}`, JSON.stringify({
        localMemberPermission: hasMemberPermission1,
        localOwnerPermission: hasPermission1,
      }));
    }
  }, [type, getUserId, projectId]);

  useEffect(() => {
    init();
  }, [init]);

  const value = {
    ...props,
    prefixCls: 'c7ncd-cluster',
    intlPrefix: 'infra.code.lib',
    permissions: [
      'choerodon.code.project.infra.code-lib-management.ps.project-member',
      'choerodon.code.project.infra.code-lib-management.ps.project-owner',
    ],
    tabs,
    managementStore,
    hasMemberPermission,
    hasPermission,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
