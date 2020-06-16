import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
// import SelectApp from '../tool-bar';
import PsApproval from './PsApproval';
import Modals from '../modals';

import '../index.less';

const CodeManagerBranch = observer(() => (
  <TabPage>
    <Modals type="psApproval" />
    <CodeManagerHeader />
    {/* <SelectApp /> */}
    <PsApproval />
  </TabPage>));

export default CodeManagerBranch;
