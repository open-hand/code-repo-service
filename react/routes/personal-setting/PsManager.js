/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React from 'react';
import { PageWrap, PageTab, Page } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { usPsManagerStore } from './stores';
import GitLabSetting from './gitlab-setting';
import SvnSetting from './svn-setting';
import ProdSetting from './prod-setting';
import ThirdPartServiceBind from './third-part-service-bind';
import './index.less';

const PsManager = observer(() => {
  const { intl: { formatMessage } } = usPsManagerStore();

  return (
    // <Page service={detailPermissions} >
    <Page
      // service={['choerodon.code.person.setting.personal-setting.ps.default']}
      className="c7n-infra-personal-setting"
    >
      <PageWrap noHeader={['gitLabSetting', 'svnSetting']}>
        <PageTab
          // title="代码库（GITLAB）设置"
          title={formatMessage({ id: 'infra.personal.message.gitLabSetting' })}
          tabKey="gitLabSetting"
          route="/rducm/personal-setting/gitlab"
          component={GitLabSetting}
          alwaysShow
        />
        <PageTab
          // title="文档库（SVN）设置"
          title={formatMessage({ id: 'infra.personal.message.svnSetting' })}
          tabKey="svnSetting"
          route="/rducm/personal-setting/svn"
          component={SvnSetting}
          alwaysShow
        />
        <PageTab
          title={formatMessage({ id: 'infra.personal.message.prodSetting' })}
          tabKey="prodSetting"
          route="/rducm/personal-setting/product"
          component={ProdSetting}
          alwaysShow
        />
        <PageTab
          title="第三方服务绑定"
          tabKey="thirdPartServiceBind"
          route="/rducm/personal-setting/thirdPartServiceBind"
          component={ThirdPartServiceBind}
          alwaysShow
        />
      </PageWrap>
    </Page>);
});

export default PsManager;
