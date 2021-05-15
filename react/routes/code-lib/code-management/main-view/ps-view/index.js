import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage, Content } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
// import SelectApp from '../tool-bar';
import PsOverView from './PsOverView';
import Modals from '../modals';

import '../index.less';

const CodeManagerBranch = observer(() => (
  <TabPage>
    <Modals type="psView" />
    <CodeManagerHeader />
    {/* <SelectApp /> */}
    <Content>
      <PsOverView />
    </Content>
  </TabPage>));

export default CodeManagerBranch;
