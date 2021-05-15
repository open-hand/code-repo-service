import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage, Content } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
import PsApproval from './ApplyView';
import Modals from '../modals';

import '../index.less';

const CodeManagerBranch = observer(() => (
  <TabPage>
    <Modals type="applyView" />
    <CodeManagerHeader />
    <Content>
      <PsApproval />
    </Content>
  </TabPage>));

export default CodeManagerBranch;
