/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable max-len */
import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import FormDataSet from './FormDataSet';
import PathListDataSet from './PathListDataSet';
import UserDataSet from './UserDataSet';
import UserPathListDataSet from './UserPathListDs';

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
    openType,
  } = props;

  // 人员options  DataSet
  const userOptions = useMemo(() => new DataSet(UserDataSet({
    organizationId, projectId, type: openType,
  })), [projectId]);
  const pathListDs = useMemo(() => new DataSet(PathListDataSet({
    formatMessage, intlPrefix, userOptions,
  })), [projectId]);


  const userOptionsPermission = useMemo(() => new DataSet(UserDataSet({
    organizationId, projectId, type: openType,
  })), [projectId]);
  const UserPathListDS = useMemo(() => new DataSet(UserPathListDataSet({
    formatMessage, intlPrefix, userOptionsPermission,
  })), [projectId]);

  const formDs = useMemo(() => new DataSet(FormDataSet({
    formatMessage, intlPrefix, pathListDs, organizationId, projectId, branchServiceDs, currentBranchAppId, openType, UserPathListDS,
  })), [organizationId, projectId, currentBranchAppId, branchServiceDs]);

  useEffect(() => {
    formDs.create();
    pathListDs.create();
    UserPathListDS.create();
    return () => {
      formDs.reset();
      pathListDs.reset();
      UserPathListDS.reset();
    };
  }, []);
  const value = {
    ...props,
    formDs,
    pathListDs,
    userOptions,
    UserPathListDS,
    userOptionsPermission,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

