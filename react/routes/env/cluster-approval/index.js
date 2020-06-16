import React from 'react';
import { StoreProvider } from './stores';
import ClusterApproval from './ClusterApproval';

export default (props) => (
  <StoreProvider {...props}>
    <ClusterApproval />
  </StoreProvider>
);
