import React from 'react';
import { StoreProvider } from './stores';
import Content from './Content';

const index = (props:any) => (
  <StoreProvider {...props}>
    <Content />
  </StoreProvider>
);

export default index;
