/* eslint-disable */
import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import { Button, Form, Modal as OldModal, Badge, Spin, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import { Content, Header, TabPage, axios, Choerodon, Breadcrumb, HeaderButtons, Page } from '@choerodon/boot';
import './BaseInfo.less';
import { usPsManagerStore } from '../stores';

const resetGitlabKey = Modal.key();

function BaseInfo() {
  const context = usPsManagerStore();
  const { UserInfoStore, intl, prefixCls,formatClient,
    formatCommon, } = context;
  const [enablePwd, setEnablePwd] = useState({});
  const [gitLabInfo, setGitLabInfo] = useState({});
  // const [isFailed, setFileFlag] = useState(false);
  const [loading, setLoading] = useState(false);
  const {
    glAvatarUrl, glUsername, glName, glState, glWebUrl, userId,
  } = gitLabInfo;

  const loadGitLabInfo = () => {
    setLoading(true);
    axios.get('/rducm/v1/gitlab/users/self')
      .then((response) => {
        setGitLabInfo(response);
        setLoading(false);
        if (response.failed) {
          // setFileFlag(true);
          Choerodon.prompt(response.message);
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        setLoading(false);
      });
  };
  const loadEnablePwd = () => {
    axios.get('/iam/choerodon/v1/system/setting/enable_resetPassword')
      .then((response) => {
        setEnablePwd(response);
      });
  };

  function renderAvatar(avatarUrl) {
    const image = avatarUrl && {
      backgroundImage: `url(${Choerodon.fileServer(avatarUrl)})`,
    };
    return (
      <div className={`${prefixCls}-avatar-wrap`}>
        <div
          className={`${prefixCls}-avatar`}
          style={image || {}}
        >
          {!avatarUrl && glName && glName.charAt(0)}
        </div>
      </div>
    );
  }

  function enableRender(v) {
    return (
      <Badge
        status={v === 'active' ? 'success' : 'error'}
        text={
          v === 'active' ? formatCommon({ id: 'enable' }) : formatCommon({ id: 'disable' })
        }
      />
    );
  }
  function goToGitlab() {
    const { resetGitlabPasswordUrl } = enablePwd;
    if (enablePwd.enable_reset) {
      window.open(resetGitlabPasswordUrl);
    }
  }
  function handleCopy() {
    Choerodon.prompt(formatCommon({ id: 'copy.success' }));
  }
  async function handleResetGitlab(modal) {
    modal.update({
      children: <Spin spinning />,
      footer: null,
    });
    try {
      const res = await UserInfoStore.resetPassword(userId);
      if (res && !res.falied) {
        const children = (
          <div className="gitlab-user-info-reset-content">
            <span>{intl.formatMessage({ id: 'infra.personal.view.resetInfo' })}：</span>
            <span className="gitlab-user-info-reset-content-password">{res}</span>
            <CopyToClipboard
              text={res}
              onCopy={handleCopy}
            >
              <Icon type="content_copy" className="gitlab-user-info-reset-content-icon" />
            </CopyToClipboard>
            <div>{intl.formatMessage({ id: 'infra.personal.view.resetTips' })}</div>
          </div>
        );
        modal.update({
          children,
          okText: intl.formatMessage({ id: 'infra.personal.view.toUpdate' }),
          onOk: goToGitlab,
          footer: (okBtn, cancelBtn) => (
            <div>
              {cancelBtn}
              {okBtn}
            </div>
          ),
        });
      } else {
        modal.update({
          children: <Spin spinning />,
          footer: (okBtn, cancelBtn) => cancelBtn,
        });
      }
    } catch (e) {
      modal.update({
        children: <Spin spinning />,
        footer: (okBtn, cancelBtn) => cancelBtn,
      });
    }
    return false;
  }
  function openResetGitlab() {
    if (enablePwd.enable_reset) {
      const resetModal = Modal.open({
        key: resetGitlabKey,
        title: intl.formatMessage({ id: 'infra.personal.operate.resetPassword' }),
        children: intl.formatMessage({ id: 'infra.personal.operate.updateInfo' }),
        okText: intl.formatMessage({ id: 'reset' }),
        movable: false,
        onOk: () => handleResetGitlab(resetModal),
      });
    } else {
      Modal.open({
        key: Modal.key(),
        title: intl.formatMessage({ id: 'infra.personal.operate.resetPassword' }),
        children: intl.formatMessage({ id: 'infra.personal.message.noUrl' }),
        okText: intl.formatMessage({ id: 'infra.personal.message.iKnow' }),
      });
    }
  }

  function renderUserInfo() {
    return (
      <React.Fragment>
        <div className={`${prefixCls}-top-container`}>
          <div className={`${prefixCls}-avatar-wrap-container`}>
            {renderAvatar(glAvatarUrl)}
          </div>
          <div className={`${prefixCls}-login-info`}>
            <div>{glName}</div>
            <div>{formatCommon({ id: 'states' })}: {enableRender(glState)}</div>
            <div style={{ fontSize: 13, fontWeight: 'bold' }}>{formatCommon({ id: 'username' })}：{glUsername}</div>
          </div>
        </div>
        <div className={`${prefixCls}-info-container`} style={{paddingLeft:178,lineHeight:'24px'}}>
          <div className={`${prefixCls}-info-container-account`}>
              <div>
                <span className="gitlab-user-info-info-container-account-title">{formatClient({ id: 'homepage' })}</span>
                <a href={glWebUrl} rel="nofollow me noopener noreferrer" target="_blank" className={`${prefixCls}-info-container-account-content`}>{glWebUrl}</a>
              </div>
          </div>
        </div>
      </React.Fragment>
    );
  }

  function handleUpdateStore() {
    if (enablePwd.enable_reset) {
      Modal.open({
        key: Modal.key(),
        title: intl.formatMessage({ id: 'infra.personal.operate.updatePassword' }),
        children: intl.formatMessage({ id: 'infra.personal.message.modifyConfirm' }),
        okText: intl.formatMessage({ id: 'edit' }),
        onOk: () => {
          const { resetGitlabPasswordUrl } = enablePwd;
          if (enablePwd.enable_reset) {
            window.open(resetGitlabPasswordUrl);
          }
        },
      });
    } else {
      Modal.open({
        key: Modal.key(),
        title: intl.formatMessage({ id: 'infra.personal.operate.updatePassword' }),
        children: intl.formatMessage({ id: 'infra.personal.message.noUrl' }),
        okText: intl.formatMessage({ id: 'infra.personal.message.iKnow' }),
      });
    }
  }

  useEffect(() => {
    loadGitLabInfo();
    loadEnablePwd();
  }, []);

  const render = () => {
    const user = UserInfoStore.getUserInfo;
    return (
      <Page>
        <Header className={`${prefixCls}-header`}>
          <HeaderButtons
            showClassName={false}
            items={([{
              name: formatClient({ id: 'editPsw' }),
              icon: 'mode_edit',
              display: true,
              handler: handleUpdateStore.bind(this),
            }, {
              name: formatClient({ id: 'resetPsw' }),
              icon: 'swap_horiz',
              display: true,
              handler: openResetGitlab,
            }])}
          />
        </Header>
        <Breadcrumb />
        <Content className={`${prefixCls}-container`}>
          {renderUserInfo(user)}
        </Content>
      </Page>
    );
  };
  return render();
}
export default Form.create({})(observer(BaseInfo));
