/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect } from 'react';
import { Page, Action, Choerodon } from '@choerodon/boot';
import { Table, Modal } from 'choerodon-ui/pro';
import { Tooltip } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { isNil } from 'lodash';
import TimePopover from '@/components/time-popover/TimePopover';
import UserAvatar from '@/components/user-avatar';
import { usPsManagerStore } from '../stores';
import Sider from '../modals/ps-set';

const { Column } = Table;
const intlPrefix2 = 'infra.codeManage.ps';

const modalKey = Modal.key();

const PsSet = observer(() => {
  const {
    intlPrefix,
    // prefixCls,
    intl: { formatMessage },
    // appServiceDs,
    psSetDs,
    appId,
    AppState: { currentMenuType: { id: projectId, organizationId } },
    overStores,
    hasPermission,
  } = usPsManagerStore();
  const modalProps = {
    modify: {
      okText: formatMessage({ id: 'save' }),
      title: formatMessage({ id: `${intlPrefix2}.message.modifyPs` }),
    },
  };

  function refresh() {
    psSetDs.query();
  }

  useEffect(() => {
    refresh();
  }, [appId]);

  function renderTime({ value }) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }

  // function renderRole({ value }) {
  //   const text = map(value || [], 'name');
  //   return text.join();
  // }

  function handleDelete() {
    const record = psSetDs.current;
    const mProps = {
      title: formatMessage({ id: `${intlPrefix}.permission.delete.title` }),
      children: formatMessage({ id: `${intlPrefix}.permission.delete.des` }),
      okText: formatMessage({ id: 'delete' }),
      okProps: { color: 'red' },
      cancelProps: { color: 'dark' },
    };
    psSetDs.delete(record, mProps);
  }
  async function handleAsync() {
    const record = psSetDs.current;
    await overStores.asyncUser(organizationId, projectId, record.get('repositoryId'), record.get('id'))
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
          return false;
        } else {
          Choerodon.prompt(formatMessage({ id: `${intlPrefix2}.message.asyncSuccess` }));
          psSetDs.query();
          return true;
        }
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        return false;
      });
  }

  function renderAction({ record }) {
    const actionData = [{
      service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
      text: formatMessage({ id: 'delete' }),
      action: handleDelete,
    }];
    const asyncData = [{
      service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
      text: <Tooltip title={formatMessage({ id: `${intlPrefix2}.message.asyncTips` })}>{formatMessage({ id: 'async' })}</Tooltip>,
      action: handleAsync,
    }];
    // 分支如果是未同步，不允许编辑和删除
    if (!record.get('syncGitlabFlag')) {
      return <Action data={asyncData} />;
    }
    // 权限层级大于50，不允许编辑和删除
    if (Number(record.get('glAccessLevel').substring(1)) >= 50) {
      return null;
    }
    return <Action data={actionData} />;
  }

  function handleTableFilter(record) {
    return record.status !== 'add';
  }


  function handleSave() {
    psSetDs.query();
  }

  function openModal(type) {
    Modal.open({
      ...modalProps[type],
      children: <Sider
        type={type}
        onOk={handleSave}
        psSetDs={psSetDs}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: 'base-lang-sider',
    });
  }

  function renderName({ record }) {
    const avatar = (
      <div style={{ display: 'inline-flex' }}>
        <UserAvatar
          user={{
            id: record.get('user').userId,
            loginName: record.get('user').loginName,
            realName: record.get('user').realName,
            imageUrl: record.get('user').imageUrl,
            email: record.get('user').email,
          }}
          hiddenText
        />
      </div>
    );
    const avatar2 = (
      <div style={{ display: 'inline-flex' }}>
        <UserAvatar
          user={{
            id: record.get('user').userId,
            loginName: record.get('user').loginName,
            realName: record.get('user').realName,
            imageUrl: record.get('user').imageUrl,
            email: record.get('user').email,
          }}
        />
      </div>
    );

    if (record.get('syncGitlabFlag')) {
      return (
        (Number(record.get('glAccessLevel').substring(1)) < 50 && hasPermission) ? (
          <React.Fragment>{avatar}<span onClick={() => openModal('modify')} className="c7n-infra-code-management-table-name">{record.get('user').realName}</span></React.Fragment>
        ) : avatar2
      );
    } else {
      return (
        <div style={{ display: 'inline-flex' }}>
          <UserAvatar
            user={{
              id: record.get('user').userId,
              loginName: record.get('user').loginName,
              realName: record.get('user').realName,
              imageUrl: record.get('user').imageUrl,
              email: record.get('user').email,
            }}
          />
          <div className="assign-member-external-user">
            <span className="assign-member-external-user-text">
              未同步
            </span>
          </div>
        </div>
      );
    }
  }
  function renderLevel({ text, record }) {
    if (record.get('syncGitlabFlag') && Number(record.get('glAccessLevel').substring(1)) < 50 && hasPermission) {
      return (
        <span onClick={() => openModal('modify')} className="c7n-infra-code-management-table-name">{text}</span>
      );
    } else {
      return (
        <span>{text}</span>
      );
    }
  }

  function renderRole({ value }) {
    return value.join();
  }

  return (
    <Page
      className="c7n-infra-code-management-table"
      service={[
        'choerodon.code.project.infra.code-lib-management.ps.project-owner',
        'choerodon.code.project.infra.code-lib-management.ps.project-member',
      ]}
    >
      <Table
        dataSet={psSetDs}
        filter={handleTableFilter}
        queryBar="bar"
        queryFieldsLimit={3}
      >
        <Column name="realName" renderer={renderName} width={200} />
        <Column renderer={renderAction} width={70} />
        <Column name="loginName" />
        <Column name="repositoryName" />
        <Column name="roleNames" renderer={renderRole} />
        <Column name="glAccessLevel" renderer={renderLevel} />
        <Column name="glExpiresAt" />
        <Column
          name="createdByName"
          renderer={({ record }) => (
            <div style={{ display: 'inline-flex' }}>
              <UserAvatar
                user={{
                  id: record.get('createdUser').userId,
                  loginName: record.get('createdUser').loginName,
                  realName: record.get('createdUser').realName,
                  imageUrl: record.get('createdUser').imageUrl,
                  email: record.get('createdUser').email,
                }}
              // hiddenText
              />
            </div>
          )}
        />
        <Column name="lastUpdateDate" renderer={renderTime} />
      </Table>
    </Page>
  );
});

export default PsSet;
