/* eslint-disable max-len */
import React, {
  createContext, useCallback, useContext, useMemo,
} from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { injectIntl } from 'react-intl';
import { inject } from 'mobx-react';
import { DataSetSelection } from 'choerodon-ui/pro/lib/data-set/enum';
import useStore, { MainStoreProps } from './useStore';

interface ContextProps {
  prefixCls: string,
  intlPrefix: string,
  formatMessage(arg0: object, arg1?: object): string,
  projectId: number,
  customTabsData: {
    name:string,
    value: string
  }[],
  psAllStore: MainStoreProps
}

const Store = createContext({} as ContextProps);

export function usePermissionStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props: any) => {
  const {
    children,
    intl: { formatMessage },
    AppState: { currentMenuType: { projectId } },
  } = props;

  const psAllStore = useStore();

  const customTabsData = [
    {
      name: '权限分配',
      value: 'psSet',
    },
    {
      name: '权限申请',
      value: 'applyView',
    },
    {
      name: '权限审批',
      value: 'psApproval',
    },
    {
      name: '权限审计',
      value: 'psAudit'
    }
  ]

  const value = {
    ...props,
    formatMessage,
    projectId,
    customTabsData,
    psAllStore
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
