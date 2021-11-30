/**
 * 权限管理
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React from 'react';
import { has } from '@choerodon/inject';
import { PageWrap, PageTab, Page } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { usPsManagerStore } from './stores';
import GitLabSetting from './gitlab-setting';
import SvnSetting from './svn-setting';
import ProdSetting from './prod-setting';
import ThirdPartServiceBind from './third-part-service-bind';
import './index.less';

const PsManager = observer(() => {
  const {
    formatClient,
  } = usPsManagerStore();

  const pageTabArr = [
    <PageTab
      title={formatClient({ id: 'code' })}
      tabKey="gitLabSetting"
      route="/rducm/personal-setting/gitlab"
      component={GitLabSetting}
      alwaysShow
    />,
    <PageTab
      title={formatClient({ id: 'svn' })}
      tabKey="svnSetting"
      route="/rducm/personal-setting/svn"
      component={SvnSetting}
      alwaysShow
    />,
    <PageTab
      title={formatClient({ id: 'artifact' })}
      tabKey="prodSetting"
      route="/rducm/personal-setting/product"
      component={ProdSetting}
      alwaysShow
    />,
    has('base-pro:thirdPartServiceBind') && <PageTab
      title={formatClient({ id: 'artifact' })}
      tabKey="thirdPartServiceBind"
      route="/rducm/personal-setting/thirdPartServiceBind"
      component={ThirdPartServiceBind}
      alwaysShow
    />,
  ].filter(Boolean);

  return (
    // <Page service={detailPermissions} >
    <Page
      // service={['choerodon.code.person.setting.personal-setting.ps.default']}
      className="c7n-infra-personal-setting"
    >
      <PageWrap noHeader={['gitLabSetting', 'svnSetting']}>
        {pageTabArr}
      </PageWrap>
    </Page>
  );
});

export default PsManager;
