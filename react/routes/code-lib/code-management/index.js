import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter } from '@choerodon/boot';
import { StoreProvider } from './stores';

const TabPage = asyncRouter(() => import('./Management'));

const Index = (props) => (
  <StoreProvider {...props}>
    <Switch>
      <Route component={TabPage} path={props.match.url} />
    </Switch>
  </StoreProvider>
);

export default Index;

