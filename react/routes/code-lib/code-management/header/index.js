import React from 'react';
import { observer } from 'mobx-react-lite';
import { Breadcrumb } from '@choerodon/boot';
import './index.less';

const CodeManagerHeader = observer(() => (
  <div className="code-management-header">
    <Breadcrumb className="code-management-header-no-bottom" />
  </div>));

export default CodeManagerHeader;
