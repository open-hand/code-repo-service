import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import { Button, Form, Modal as OldModal, Badge, Spin, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import { Content, Header, TabPage, axios, Choerodon, Breadcrumb } from '@choerodon/boot';
import './BaseInfo.less';
import { usPsManagerStore } from '../stores';

const resetGitlabKey = Modal.key();

function BaseInfo() {
  const context = usPsManagerStore();
  const { UserInfoStore, intl, prefixCls } = context;
  const [enablePwd, setEnablePwd] = useState({});
  const [gitLabInfo, setGitLabInfo] = useState({});
  const [isFailed, setFileFlag] = useState(false);
  const [loading, setLoading] = useState(false);
  const { glAvatarUrl, glUsername, glName, glState, glWebUrl, userId } = gitLabInfo;

  const loadGitLabInfo = () => {
    setLoading(true);
    axios.get('/rducm/v1/gitlab/users/self')
      .then((response) => {
        setGitLabInfo(response);
        setLoading(false);
        if (response.failed) {
          setFileFlag(true);
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
          v === 'active' ? intl.formatMessage({ id: 'active' }) : intl.formatMessage({ id: 'disable' })
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
    Choerodon.prompt(intl.formatMessage({ id: 'success.copy' }));
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
    const resetModal = Modal.open({
      key: resetGitlabKey,
      title: intl.formatMessage({ id: 'infra.personal.operate.resetPassword' }),
      children: intl.formatMessage({ id: 'infra.personal.operate.updateInfo' }),
      okText: intl.formatMessage({ id: 'reset' }),
      movable: false,
      onOk: () => handleResetGitlab(resetModal),
    });
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
            <div>{intl.formatMessage({ id: 'status' })}: {enableRender(glState)}</div>
            <div style={{ fontSize: 13, fontWeight: 'bold' }}>{intl.formatMessage({ id: 'userName' })}：{glUsername}</div>
          </div>
        </div>
        <div className={`${prefixCls}-info-container`}>
          <div className={`${prefixCls}-info-container-account`}>
            <div>
              <div>
                <span className="gitlab-user-info-info-container-account-title">{intl.formatMessage({ id: 'infra.personal.model.glWebUrl' })}</span>
                <a href={glWebUrl} rel="nofollow me noopener noreferrer" target="_blank" className={`${prefixCls}-info-container-account-content`}>{glWebUrl}</a>
              </div>
              {/* <div>
                <span className="gitlab-user-info-info-container-account-title">{intl.formatMessage({ id: 'infra.personal.model.createdAt' })}</span>
                <span style={{ marginLeft: '.43rem' }} className={`${prefixCls}-info-container-account-content`}>{glCreatedAt}</span>
              </div>
              { !resetPasswordFlag && !isFailed && (
                <div>
                  <span className="gitlab-user-info-info-container-account-title">{intl.formatMessage({ id: 'infra.personal.model.initPassword' })}</span>
                  <Password style={{ marginLeft: '.33rem' }} value={initPassword} className="psw-border" />
                </div>
              )} */}
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  }

  function handleUpdateStore() {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: intl.formatMessage({ id: 'infra.personal.message.modifyGitlabPassword' }),
      content: intl.formatMessage({ id: 'infra.personal.message.modifyConfirm' }),
      okText: intl.formatMessage({ id: 'edit' }),
      width: 560,
      onOk: () => {
        const { resetGitlabPasswordUrl } = enablePwd;
        if (enablePwd.enable_reset) {
          window.open(resetGitlabPasswordUrl);
        }
      },
    });
  }

  useEffect(() => {
    loadGitLabInfo();
    loadEnablePwd();
  }, []);

  const render = () => {
    const user = UserInfoStore.getUserInfo;
    return (
      <TabPage>
        <Spin spinning={loading || false} >
          <Header className={`${prefixCls}-header`}>
            <Button
              className="gitlab-user-info-header-btn"
              onClick={handleUpdateStore.bind(this)}
              icon="mode_edit"
              disabled={!enablePwd.enable_reset || isFailed}
            >
              {intl.formatMessage({ id: 'infra.personal.operate.updatePassword' })}
            </Button>
            <Button
              onClick={openResetGitlab}
              icon="swap_horiz"
              className="gitlab-user-info-header-btn"
              style={{
                textTransform: 'none',
              }}
            >
              {intl.formatMessage({ id: 'infra.personal.operate.resetPassword' })}
            </Button>
          </Header>
          <Breadcrumb />
          <Content className={`${prefixCls}-container`}>
            {renderUserInfo(user)}
          </Content>
        </Spin>
      </TabPage>
    );
  };
  return render();
}
export default Form.create({})(observer(BaseInfo));
