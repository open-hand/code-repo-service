
import React from 'react';
import { StoreProvider } from './stores';
import PsManager from './PsManager';

export default (props) => (
  <StoreProvider {...props}>
    <PsManager />
  </StoreProvider>
);
