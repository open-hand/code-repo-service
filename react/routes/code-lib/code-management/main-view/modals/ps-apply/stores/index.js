import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import FormDataSet from './FormDataSet';

const Store = createContext();

export function useAddMemberStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: { currentMenuType: { projectId, organizationId }, userInfo: { id: userId, realName } },
    intl: { formatMessage },
    intlPrefix,
    branchServiceDs,
    currentBranchAppId,
  } = props;

  const formDs = useMemo(() => new DataSet(FormDataSet({ formatMessage, intlPrefix, organizationId, projectId, userId, realName, branchServiceDs, currentBranchAppId })), [currentBranchAppId, organizationId, projectId, userId, branchServiceDs]);

  useEffect(() => {
    formDs.create();
  }, []);

  const value = {
    ...props,
    formDs,
    projectId,
    organizationId,
    realName,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

