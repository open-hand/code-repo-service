import React from 'react';
import { StoreProvider } from './stores';
import MainView from './MainView';

const index = props => (
  <StoreProvider {...props}>
    <MainView />
  </StoreProvider>
);


export default index;
