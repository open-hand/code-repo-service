import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import { asyncRouter, asyncLocaleProvider, nomatch } from '@choerodon/boot';
import { ModalContainer } from 'choerodon-ui/pro';

import './index.less';

// 个人设置
const personalSetting = asyncRouter(() => import('./routes/personal-setting'));

// 统一代码库
const codeManagement = asyncRouter(() => import('./routes/code-lib/code-management'));
const codeLibAudit = asyncRouter(() => import('./routes/code-lib/code-lib-audit'));


function RDUCMIndex({ match, AppState: { currentLanguage: language } }) {
  const IntlProviderAsync = asyncLocaleProvider(language, () => import(`./locale/${language}`));
  return (
    <IntlProviderAsync>
      <div className="hrds-code-repo">
        <Switch>
          <Route path={`${match.url}/personal-setting`} component={personalSetting} />
          <Route path={`${match.url}/code-lib-management`} component={codeManagement} /> {/* 代码库项目级页面，包括很多tab页 */}
          <Route path={`${match.url}/code-lib-org`} component={codeLibAudit} /> {/* 代码库库组织级审计页面，包括很多tab页 */}
          <Route path="*" component={nomatch} />
        </Switch>
        <ModalContainer />
      </div>
    </IntlProviderAsync>
  );
}

export default inject('AppState')(RDUCMIndex);
