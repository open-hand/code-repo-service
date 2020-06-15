import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Form, Spin } from 'choerodon-ui';
import { Modal, Password } from 'choerodon-ui/pro';
import { Content, Header, TabPage, Choerodon, axios, Breadcrumb } from '@choerodon/boot';
import './BaseInfo.less';
import { usPsManagerStore } from '../stores';
import EditPassword from './EditPassword';


const createKey = Modal.key();
function BaseInfo() {
  const context = usPsManagerStore();
  const { SvnInfoStore, intl, intlPrefix, prefixCls, AppState } = context;
  const { organizationId, imageUrl, loginName } = AppState.userInfo;
  const [loading, setLoading] = useState(false);
  const [enablePwd, setEnablePwd] = useState({});
  const { name, userName, creationDate, userPassword } = enablePwd;
  // const [avatar, setAvatar] = useState('');
  const modalRef = React.createRef();
  // const loadUserInfo = () => {
  //   setLoading(true);
  //   AppState.loadUserInfo().then(data => {
  //     AppState.setUserInfo(data);
  //     SvnInfoStore.setUserInfo(AppState.getUserInfo);
  //     setLoading(false);
  //     // setAvatar(SvnInfoStore.getAvatar);
  //   });
  // };

  const loadEnablePwd = () => {
    setLoading(true);
    // TODO
    axios.get(`/rdudm/v1/${organizationId}/doc-users/selectDefaultPwd/${loginName}`)
    // axios.get(`/rdudm/v1/${organizationId}/doc-users/selectDefaultPwd/4`)
      .then((response) => {
        setEnablePwd(response);
        setLoading(false);
        if (response.failed) {
          Choerodon.prompt(response.message);
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        setLoading(false);
      });
    // .then((response) => {
    //   setEnablePwd(response);
    // });
  };

  function renderAvatar() {
    const image = imageUrl && {
      backgroundImage: `url(${Choerodon.fileServer(imageUrl)})`,
    };
    return (
      <div className={`${prefixCls}-avatar-wrap`}>
        <div
          className={`${prefixCls}-avatar`}
          style={image || {}}
        >
          {!imageUrl && name && name.charAt(0)}
        </div>
      </div>
    );
  }

  function renderUserInfo() {
    // const { status, webUrl, creationDate, userPassword } = user;
    return (
      <React.Fragment>
        {/* <div className={`${prefixCls}-top-container`}>
          <div className={`${prefixCls}-avatar-wrap-container`}>
            {renderAvatar(user)}
          </div>
          <div className={`${prefixCls}-login-info`}>
            <div>{name}</div>
            <div style={{ fontSize: 13, fontWeight: 'bold' }}>{intl.formatMessage({ id: 'userName' })}：{userName}</div>
          </div>
        </div> */}
        <div className={`${prefixCls}-top-container`}>
          <div className={`${prefixCls}-avatar-wrap-container`}>
            {renderAvatar(imageUrl)}
          </div>
          <div className={`${prefixCls}-login-info`}>
            <div>{name}</div>
            <div style={{ fontSize: 13, fontWeight: 'bold' }}>{intl.formatMessage({ id: 'userName' })}：{userName}</div>
          </div>
        </div>
        <div className={`${prefixCls}-info-container`}>

          <div className={`${prefixCls}-info-container-account`}>
            <div>
              {/* <div>
                <span className={`${prefixCls}-info-container-account-title`}>SVN地址</span>
                <span href={svnUrl} rel="nofollow me noopener noreferrer" target="_blank" className={`${prefixCls}-info-container-account-content`}>{svnUrl}</span>
              </div> */}
              <div>
                <span className={`${prefixCls}-info-container-account-title`}>{intl.formatMessage({ id: 'infra.personal.model.createdAt' })}</span>
                <span className={`${prefixCls}-info-container-account-content`}>{creationDate}</span>
              </div>
              { enablePwd.pwdUpdateFlag !== 1 && (
                <div>
                  <span className={`${prefixCls}-info-container-account-title`} style={{ marginRight: '.95rem' }}>{intl.formatMessage({ id: 'infra.personal.model.initPassword' })}</span>
                  <Password value={userPassword} />
                </div>
              )}
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  }


  function handleUpdatePassword() {
    // const user = SvnInfoStore.getUserInfo;
    Modal.open({
      key: createKey,
      title: intl.formatMessage({ id: 'user.changepwd.header.title' }),
      style: {
        width: 380,
      },
      drawer: true,
      children: (
        <EditPassword
          intl={intl}
          intlPrefix={intlPrefix}
          forwardref={modalRef}
          organizationId={organizationId}
          UserInfoStore={SvnInfoStore}
          userName={userName}
          name={name}
          setEnablePwd={setEnablePwd}
        />
      ),
      okText: intl.formatMessage({ id: 'save' }),
      onOk: () => {
        modalRef.current.handleSubmit();
        return false;
      },
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          {cancelBtn}
        </div>
      )
      ,
    });
  }

  useEffect(() => {
    // loadUserInfo();
    loadEnablePwd();
  }, []);

  const render = () => {
    const user = SvnInfoStore.getUserInfo;
    return (
      <TabPage>
        <Spin spinning={loading || false} >
          <Header className={`${prefixCls}-header`}>
            <Button
              className="svg-setting-header-btn"
              onClick={handleUpdatePassword.bind(this)}
              icon="mode_edit"
            >
              {intl.formatMessage({ id: 'user.changepwd.header.title' })}
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
