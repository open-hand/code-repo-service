import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Form, Spin } from 'choerodon-ui';
import { Modal, Password } from 'choerodon-ui/pro';
import { Content, Header, TabPage, Choerodon, axios, Breadcrumb, HeaderButtons } from '@choerodon/boot';
import Empty from '@/components/empty';
import empty from '@/assets/empty.png';
import './BaseInfo.less';
import { usPsManagerStore } from '../stores';
import EditPassword from './EditPassword';


const createKey = Modal.key();
function BaseInfo() {
  const context = usPsManagerStore();
  const {
    SvnInfoStore, intl, intlPrefix, prefixCls, AppState,
  } = context;
  const { organizationId, imageUrl, loginName } = AppState.userInfo;
  const [loading, setLoading] = useState(false);
  const [enablePwd, setEnablePwd] = useState({});
  const [noContentFlag, setNoContentFlag] = useState(true);
  const {
    name, userName, creationDate, userPassword,
  } = enablePwd;
  const modalRef = React.createRef();

  const loadEnablePwd = async () => {
    setLoading(true);
    // TODO
    await axios.get(`/rdudm/v1/doc-users/selectDefaultPwd/${loginName}`)
      .then((response) => {
        setEnablePwd(response);
        setLoading(false);
        setNoContentFlag(response.status === 204);
        if (response.failed) {
          Choerodon.prompt(response.message);
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        setLoading(false);
      });
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
    return (
      <React.Fragment>
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
              <div>
                <span className={`${prefixCls}-info-container-account-title`}>{intl.formatMessage({ id: 'infra.personal.model.createdAt' })}</span>
                <span className={`${prefixCls}-info-container-account-content`}>{creationDate}</span>
              </div>
              {enablePwd.pwdUpdateFlag !== 1 && (
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

  async function handlePasswordChange() {
    try {
      await modalRef.current.handleSubmit(loadEnablePwd);
      return false;
    } catch (error) {
      throw new Error(error);
    }
  }


  function handleUpdatePassword() {
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
      onOk: handlePasswordChange,
      footer: (okBtn, cancelBtn) => (
        <div>
          {cancelBtn}
          {okBtn}
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
        <Header className={`${prefixCls}-header`}>
          <HeaderButtons
            showClassName={false}
            items={([{
              name: intl.formatMessage({ id: 'user.changepwd.header.title' }),
              icon: 'mode_edit',
              display: true,
              handler: handleUpdatePassword.bind(this),
              disabled: noContentFlag,
            }])}
          />
        </Header>
        <Breadcrumb />
        {noContentFlag ? (
          <Content>
            <Empty
              // loading={getLoading}
              pic={empty}
              title={intl.formatMessage({ id: 'infra.changepwd.message.svn.noContent.title' })}
              description={intl.formatMessage({ id: 'infra.changepwd.message.svn.noContent.desc' })}
            />
          </Content>
        ) :
          <Content className={`${prefixCls}-container`}>
            {renderUserInfo(user)}
          </Content>
        }
      </TabPage>
    );
  };
  return render();
}
export default Form.create({})(observer(BaseInfo));
