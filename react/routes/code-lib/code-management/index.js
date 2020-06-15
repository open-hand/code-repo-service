import React from 'react';
import Management from './Management';
import { StoreProvider } from './stores';


export default (props) => (
  <StoreProvider {...props}>
    <Management />
  </StoreProvider>
);
