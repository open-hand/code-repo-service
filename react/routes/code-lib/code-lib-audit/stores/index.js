// stores/index.js
import React, { createContext, useMemo, useContext } from 'react';
import { useFormatCommon, useFormatMessage } from '@choerodon/master';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import optLogDataSet from './optLogDataSet';
import projectListDataSet from './projectListDataSet';
import useTimeLineStore from './useTimeLineStore';
import psViewDataSet from './psViewDataSet';
import PsAuditDS from './PsAuditDS';

const Store = createContext();
export function useStore() {
  return useContext(Store);
}
export default Store;

export const TabKeyEnum = {
  PSVIEW: 'psView',
  OPTLOG: 'optLog',
  PSAUDIT: 'psAudit',
};

export const intlPrefix = 'c7ncd.code-lib-org';

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    AppState,
    children,
  } = props;
  const { id: userId } = AppState.userInfo;
  const { organizationId } = AppState.currentMenuType;

  const formatCommon = useFormatCommon();
  const formatClient = useFormatMessage(intlPrefix);

  const optLogDs = useMemo(() =>
    new DataSet(optLogDataSet(formatClient, formatCommon, organizationId)), [organizationId]);
  const projectListDs = useMemo(() => new DataSet(projectListDataSet(organizationId, userId)), []);
  // 权限查看
  const psViewDs = useMemo(() =>
    new DataSet(psViewDataSet(formatClient, formatCommon, organizationId)), [organizationId]);
  const psAuditDs = useMemo(() =>
    new DataSet(PsAuditDS(formatClient, formatCommon, organizationId)), [organizationId]);

  const value = {
    ...props,
    intlPrefix,
    optLogDs,
    projectListDs,
    psViewDs,
    timeLineStore: useTimeLineStore(),
    organizationId,
    psAuditDs,
    formatCommon,
    formatClient,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
