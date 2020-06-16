import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import TodoListDataset from './TodoListDataset';
import DoneListDataSet from './DoneListDataSet';

const intlPrefix = 'infra.env.approval';

const Store = createContext();

export function useApprovalStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    AppState,
    intl,
    children,
  } = props;
  const { projectId } = AppState.currentMenuType;
  const { formatMessage } = intl;

  const todoListDs = useMemo(() => new DataSet(TodoListDataset(intlPrefix, formatMessage, projectId)), [projectId]);
  const doneListDs = useMemo(() => new DataSet(DoneListDataSet(intlPrefix, formatMessage, projectId)), [projectId]);

  const value = {
    ...props,
    prefixCls: 'infra-env-cluster-approval',
    permissions: [],
    formatMessage,
    todoListDs,
    doneListDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));

