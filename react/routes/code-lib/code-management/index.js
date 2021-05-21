import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter } from '@choerodon/boot';
import { StoreProvider } from './stores';

const TabPage = asyncRouter(() => import('./Management'));

const Index = props => (
  <StoreProvider {...props}>
    <TabPage />
  </StoreProvider>
);

export default Index;

