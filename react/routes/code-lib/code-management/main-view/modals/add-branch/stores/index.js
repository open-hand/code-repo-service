import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import BranchFormDataSet from './BranchFormDataSet';

const Store = createContext();

export function useAddBranchStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: {
      currentMenuType: { projectId },
    },
    intl: { formatMessage },
    branchAppId,
  } = props;

  const branchFormDs = useMemo(
    () =>
      new DataSet(BranchFormDataSet(formatMessage, projectId, branchAppId)),
    [formatMessage, projectId, branchAppId],
  );

  useEffect(() => {
    branchFormDs.create();
  }, []);

  const value = {
    ...props,
    branchFormDs,
  };
  return <Store.Provider value={value}>{children}</Store.Provider>;
}));
