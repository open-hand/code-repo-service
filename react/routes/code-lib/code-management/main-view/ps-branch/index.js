import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage, Content } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
// import SelectApp from '../tool-bar';
import PsBranch from './PsBranch';
import Modals from '../modals';

import '../index.less';

const CodeManagerBranch = observer(() => (
  <TabPage>
    <Modals type="psBranch" />
    <CodeManagerHeader />
    <Content>
      <PsBranch />
    </Content>
    {/* <SelectApp type="psBranch" /> */}
  </TabPage>));

export default CodeManagerBranch;
