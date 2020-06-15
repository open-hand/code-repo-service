// stores/index.js
import React, { createContext, useMemo, useContext } from 'react';
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

export const intlPrefix = 'infra.codelib.audit';

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    AppState,
    intl,
    children,
  } = props;
  const { id: userId } = AppState.userInfo;
  const { organizationId } = AppState.currentMenuType;
  const { formatMessage } = intl;
  
  const optLogDs = useMemo(() => new DataSet(optLogDataSet(intlPrefix, formatMessage, organizationId)), [formatMessage, organizationId]);
  const projectListDs = useMemo(() => new DataSet(projectListDataSet(organizationId, userId)), [formatMessage]);
  // 权限查看
  const psViewDs = useMemo(() => new DataSet(psViewDataSet(intlPrefix, formatMessage, organizationId)), [formatMessage, organizationId]);
  const psAuditDs = useMemo(() => new DataSet(PsAuditDS(formatMessage, organizationId)), [formatMessage, organizationId]);

  const value = {
    ...props,
    intlPrefix,
    optLogDs,
    projectListDs,
    psViewDs,
    timeLineStore: useTimeLineStore(),
    organizationId,
    psAuditDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
