import React, { createContext, useContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import useStore from './useStore';
import ApplyListDS from './ApplyListDS';

const Store = createContext();

export function useApplyStore() {
  return useContext(Store);
}

export const StoreProvider = withRouter(injectIntl(inject('AppState')(observer((props) => {
  const {
    AppState,
    intl,
    children,
  } = props;
  const { projectId, organizationId } = AppState.currentMenuType;
  const { formatMessage } = intl;
  const intlPrefix = 'infra.env.cluster';

  const applyStore = useStore();
  const applyListDs = useMemo(() => new DataSet(ApplyListDS(intlPrefix, formatMessage, projectId, organizationId)), [organizationId, projectId]);

  const value = {
    ...props,
    AppState,
    organizationId,
    projectId,
    intlPrefix,
    applyStore,
    formatMessage,
    applyListDs,
    prefixCls: 'infra-env-cluster-apply',
    permissions: [],
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}))));

