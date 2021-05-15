import React from 'react';
import { observer } from 'mobx-react-lite';
import { Content, TabPage } from '@choerodon/boot';

import CodeManagerHeader from '../../header';
// import SelectApp from '../tool-bar';
import PsSet from './PsSet';
import Modals from '../modals';
// import HeaderButtons from '../HeaderButtons';

import '../index.less';

const CodeManagerBranch = observer(() => (
  <TabPage>
    {/* <HeaderButtons /> */}
    <Modals type="psSet" />
    <CodeManagerHeader />
    <Content>
      <PsSet type="psSet" />
    </Content>
    {/* <SelectApp /> */}
  </TabPage>));

export default CodeManagerBranch;
