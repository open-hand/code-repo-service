/* eslint-disable max-len */
import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import FormDataSet from './FormDataSet';
import PathListDataSet from './PathListDataSet';
import GlAccessLevelDataSet from './GlAccessLevelDataSet';

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
    currentBranchAppId,
  } = props;

  const glAccessLevelDataSet = useMemo(() => new DataSet(GlAccessLevelDataSet()), []);
  const pathListDs = useMemo(() => new DataSet(PathListDataSet({ formatMessage, intlPrefix })), []);
  const formDs = useMemo(() => new DataSet(FormDataSet({ formatMessage, intlPrefix, pathListDs, organizationId, projectId, branchServiceDs, currentBranchAppId })), [organizationId, projectId, branchServiceDs, currentBranchAppId]);
  const dsStore = [];

  useEffect(() => {
    formDs.create();
    pathListDs.create({ userId: [''], glAccessLevel: [''], glExpiresAt: [undefined] });
  }, []);

  const value = {
    ...props,
    prefixCls: 'add-non-project-user',
    organizationId,
    projectId,
    formDs,
    pathListDs,
    dsStore,
    glAccessLevelDataSet,
    branchServiceDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

