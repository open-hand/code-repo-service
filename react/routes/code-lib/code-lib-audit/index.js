import React from 'react';
import { StoreProvider } from './stores';
import Container from './Container';

export default function CodeLibAudit(props) {
  return (
    <StoreProvider {...props}>
      <Container />
    </StoreProvider>
  );
}
