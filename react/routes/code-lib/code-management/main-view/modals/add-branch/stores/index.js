import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import BranchNoProtectDS from './BranchNoProtectDS';
import BranchFormDataSet from './BranchFormDataSet';

const Store = createContext();

export function useAddBranchStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: { currentMenuType: { projectId } },
    intl: { formatMessage },
    branchAppId,
  } = props;

  const branchOptions = useMemo(() => new DataSet(BranchNoProtectDS(projectId, branchAppId)), [projectId, branchAppId]);
  const branchFormDs = useMemo(() => new DataSet(BranchFormDataSet(formatMessage, projectId, branchAppId, branchOptions)), [formatMessage, projectId, branchAppId, branchOptions]);

  useEffect(() => {
    branchFormDs.create();
  }, []);

  const value = {
    ...props,
    branchFormDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

