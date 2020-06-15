import React from 'react';
import { SiderStoreProvider } from './stores';
import ImportUserSider from './ImportRoleSider';

export default (props) => (
  <SiderStoreProvider {...props}>
    <ImportUserSider />
  </SiderStoreProvider>
);
