import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import FormDataSet from './FormDataSet';
import PathListDataSet from './PathListDataSet';
import UserNoDataSet from './UserNoDataSet';

const Store = createContext();

export function useAddMemberStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: { currentMenuType: { projectId, organizationId } },
    intl: { formatMessage },
    intlPrefix,
    branchServiceDs,
  } = props;

  // 无权限的人员
  const userOptions = useMemo(() => new DataSet(UserNoDataSet({ organizationId, projectId })), [projectId]);
  const pathListDs = useMemo(() => new DataSet(PathListDataSet({ formatMessage, intlPrefix, userOptions })), [projectId]);
  const formDs = useMemo(() => new DataSet(FormDataSet({ formatMessage, intlPrefix, pathListDs, organizationId, projectId, branchServiceDs })), [organizationId, projectId, branchServiceDs]);

  useEffect(() => {
    formDs.create();
    pathListDs.create();
  }, []);

  const value = {
    ...props,
    formDs,
    pathListDs,
    userOptions,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

