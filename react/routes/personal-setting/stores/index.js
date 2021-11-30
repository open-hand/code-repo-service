import React, { createContext, useContext, useMemo } from 'react';
import { useFormatCommon, useFormatMessage } from '@choerodon/master';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import UserInfoStoreObject from './UserInfoStore';
import SvnUserStoreObject from './SvnUserStore';
import useStore from './useStore';


const Store = createContext();

export function usPsManagerStore() {
  // eslint-disable-next-line react-hooks/rules-of-hooks
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const { children, AppState: { currentMenuType: { organizationId } } } = props;

  const intlPrefix = 'c7ncd.personal-setting';
  const formatClient = useFormatMessage(intlPrefix);
  const formatCommon = useFormatCommon();
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
    formatClient,
    formatCommon,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
