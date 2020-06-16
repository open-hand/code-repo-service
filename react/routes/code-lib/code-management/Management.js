import React from 'react';
import { Page } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { useManagementStore } from './stores';
import MainView from './main-view';


export default observer(() => {
  const { permission } = useManagementStore();
  return (
    <Page service={permission}>
      <MainView />
    </Page>
  );
});
