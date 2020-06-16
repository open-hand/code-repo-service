import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import UserInfoStoreObject from './UserInfoStore';
import SvnUserStoreObject from './SvnUserStore';
import useStore from './useStore';

const Store = createContext();

export function usPsManagerStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const { children, AppState: { currentMenuType: { organizationId } } } = props;


  const intlPrefix = 'user.baseinfo';
  const UserInfoStore = useMemo(() => new UserInfoStoreObject(), []);
  const SvnInfoStore = useMemo(() => new SvnUserStoreObject(organizationId), [organizationId]);
  const useSettingStore = useStore();

  const value = {
    ...props,
    prefixCls: 'svg-setting',
    intlPrefix,
    permissions: [
      'base-service.user.uploadPhoto',
    ],
    UserInfoStore,
    SvnInfoStore,
    useSettingStore,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
