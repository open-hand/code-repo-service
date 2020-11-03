import React from 'react';
import { StoreProvider } from './stores';
import Content from './AddOutsideMember';

export default props => (
  <StoreProvider {...props}>
    <Content />
  </StoreProvider>
);
