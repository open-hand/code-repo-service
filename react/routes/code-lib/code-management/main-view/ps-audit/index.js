import React from 'react';
import { observer } from 'mobx-react-lite';
import { TabPage, Content } from '@choerodon/boot';
import CodeManagerHeader from '../../header';
// import SelectApp from '../tool-bar';
import PsAudit from './PsAudit';
import Modals from '../modals';
// import HeaderButtons from '../HeaderButtons';

import '../index.less';

const CodeManagerBranch = observer(() => (
  <TabPage>
    {/* <HeaderButtons /> */}
    <Modals type="psAudit" />
    <CodeManagerHeader />
    <Content>
      <PsAudit />
    </Content>
    {/* <SelectApp /> */}
  </TabPage>));

export default CodeManagerBranch;
