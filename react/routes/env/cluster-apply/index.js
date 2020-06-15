import React from 'react';
import { StoreProvider } from './stores';
import ClusterApply from './ClusterApply';

export default (props) => (
  <StoreProvider {...props}>
    <ClusterApply />
  </StoreProvider>
);
