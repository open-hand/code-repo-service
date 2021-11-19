import React, { createContext, useContext, useMemo } from 'react';
import { injectIntl } from 'react-intl';
import { inject } from 'mobx-react';
import { DataSet } from 'choerodon-ui/pro';
import emailApproveDsConfig from './emailApproveDataSet';

interface ContextProps {
    prefixCls: string
    intl: { formatMessage(arg0: object, arg1?: object): string },
    emailApproveDataSet: DataSet,
    organizationId: string,
    modal: any,
    refresh:any
}

const Store = createContext({} as ContextProps);

export function useSaaSApproveFormStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props: any) => {
  const {
    children,
    intl: { formatMessage },
    AppState: { currentMenuType: { id, organizationId }, getUserInfo },
    modal,
    refresh
  } = props;

  const emailApproveDataSet = useMemo(() => new DataSet(emailApproveDsConfig()), []);

   emailApproveDataSet?.current?.set('email',
    getUserInfo.email);

   emailApproveDataSet?.current?.set('user_id',
    getUserInfo.id);

   const value = {
     ...props,
     prefixCls: 'c7ncd-saas-approveForm',
     emailApproveDataSet,
     organizationId,
     modal,
     refresh
   };

   return (
     <Store.Provider value={value}>
       {children}
     </Store.Provider>
   );
}));
