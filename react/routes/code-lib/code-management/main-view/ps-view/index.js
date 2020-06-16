import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage } from '@choerodon/boot';
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
    <PsOverView />
  </TabPage>));

export default CodeManagerBranch;
