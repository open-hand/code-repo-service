import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Form, Spin } from 'choerodon-ui';
import { Modal, Password } from 'choerodon-ui/pro';
import { Content, Header, TabPage, Choerodon, axios, Breadcrumb } from '@choerodon/boot';
import Empty from '@/components/empty';
import empty from '@/assets/empty.png';
import './BaseInfo.less';
import { usPsManagerStore } from '../stores';
import EditPassword from './EditPassword';

const prefixCls = 'prod-setting';

const createKey = Modal.key();
function BaseInfo() {
  const context = usPsManagerStore();
  const { useSettingStore, intl, intlPrefix, AppState } = context;
  const { organizationId, imageUrl, id, realName } = AppState.userInfo;
  const [loading, setLoading] = useState(false);
  const [enablePwd, setEnablePwd] = useState({});
  const [noContentFlag, setNoContentFlag] = useState(true);
  const { loginName, creationDate, password, pwdUpdateFlag } = enablePwd;
  const modalRef = React.createRef();

  const loadEnablePwd = () => {
    setLoading(true);
    axios.get(`/rdupm/v1/prod-users/${id}`)
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
          {!imageUrl && realName && realName.charAt(0)}
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
            <div>{realName}</div>
            <div style={{ fontSize: 13, fontWeight: 'bold' }}>{intl.formatMessage({ id: 'userName' })}：{loginName}</div>
          </div>
        </div>
        <div className={`${prefixCls}-info-container`}>

          <div className={`${prefixCls}-info-container-account`}>
            <div>
              <div>
                <span className={`${prefixCls}-info-container-account-title`}>{intl.formatMessage({ id: 'infra.personal.model.createdAt' })}</span>
                <span className={`${prefixCls}-info-container-account-content`}>{creationDate}</span>
              </div>
              {pwdUpdateFlag !== 1 && (
                <div>
                  <span className={`${prefixCls}-info-container-account-title`} style={{ marginRight: '.95rem' }}>{intl.formatMessage({ id: 'infra.personal.model.initPassword' })}</span>
                  <Password value={password} />
                </div>
              )}
            </div>
          </div>
        </div>
      </React.Fragment>
    );
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
          useStore={useSettingStore}
          loginName={loginName}
          userId={id}
          setEnablePwd={setEnablePwd}
          refresh={loadEnablePwd}
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
    loadEnablePwd();
  }, []);

  const render = () => (
    <TabPage>
      <Spin spinning={loading || false} >
        <Header className={`${prefixCls}-header`}>
          <Button
            className="prod-setting-header-btn"
            onClick={handleUpdatePassword.bind(this)}
            icon="mode_edit"
            disabled={noContentFlag}
          >
            {intl.formatMessage({ id: 'user.changepwd.header.title' })}
          </Button>
        </Header>
        <Breadcrumb />
        {noContentFlag ? (
          <Content>
            <Empty
              // loading={getLoading}
              pic={empty}
              title={intl.formatMessage({ id: 'infra.changepwd.message.prod.noContent.title' })}
              description={intl.formatMessage({ id: 'infra.changepwd.message.prod.noContent.desc' })}
            />
          </Content>
        ) :
          <Content className={`${prefixCls}-container`}>
            {renderUserInfo()}
          </Content>
        }
      </Spin>
    </TabPage>
  );
  return render();
}
export default Form.create({})(observer(BaseInfo));
