import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { Table, Tooltip, Modal } from "choerodon-ui/pro";
import {
  TableQueryBarType,
} from "choerodon-ui/pro/lib/table/enum";
import { Action, Choerodon } from '@choerodon/boot';
import { usPsManagerStore } from "../../../stores";
import UserAvatar from "@/components/user-avatar";
import Sider from '../../../modals/ps-set';
import { isNil } from 'lodash';
import TimePopover from '@/components/time-popover/TimePopover';
import CodeManagerApis from "@/routes/code-lib/code-management/apis";

const intlPrefixPSset = 'infra.codeManage.ps';

const { Column } = Table;

const modalKey = Modal.key();

const PSsetTable = () => {
  const {
    intlPrefix,
    intl: { formatMessage },
    psSetDs,
    AppState: {
      currentMenuType: { id: projectId, organizationId },
    },
    hasPermission,
  } = usPsManagerStore();

  const modalProps:any = {
    modify: {
      okText: formatMessage({ id: 'save' }),
      title: formatMessage({ id: `${intlPrefixPSset}.message.modifyPs` }),
    },
  };

  function isNBpermission(record:any){
    return record.get("syncGitlabFlag") &&
    record.get("glAccessLevel") &&
    Number(record.get("glAccessLevel").substring(1)) < 50 && hasPermission
  }

  function renderAvater(record:any, hiddenText:boolean){
    return <div style={{ display: "inline-flex" }}>
    <UserAvatar
      // @ts-expect-error
      user={{
        id: record.get("user").userId,
        loginName: record.get("user").loginName,
        realName: record.get("user").realName,
        imageUrl: record.get("user").imageUrl,
        email: record.get("user").email,
      }}
      hiddenText={hiddenText}
    />
  </div>
  }

  function renderName({ record }: any) {
    if (record.get("syncGitlabFlag")) {
      return isNBpermission(record) ? (
        <React.Fragment>
          {renderAvater(record, true)}
          <span
            onClick={() => openModal("modify")}
            className="c7n-infra-code-management-table-name"
          >
            {record.get("user").realName}
          </span>
        </React.Fragment>
      ) : (
        renderAvater(record, false)
      );
    }

    return (
      <div style={{ display: "inline-flex" }}>
        {renderAvater(record, false)}
        <div className="assign-member-external-user">
          <span className="assign-member-external-user-text">未同步</span>
        </div>
      </div>
    );
  }

  function renderLevel({ text, record }: any) {
    if (isNBpermission(record) && record.get("syncGitlabFlag")) {
      return (
        <span
          role="none"
          onClick={() => openModal("modify")}
          className="c7n-infra-code-management-table-name"
        >
          {text}
        </span>
      );
    }
    return <span>{text}</span>;
  }

  function renderRole({ value }: any) {
    return value.join();
  }

  const renderServiceName = ({ text }: any) => (
    <Tooltip title={text}>{text}</Tooltip>
  );

  const renderLoginName = ({ value, record }: any) => {
    const isLdap = record.get("ldap");
    const email = record.get("email");
    return isLdap ? value : email;
  };

  function handleSave() {
    psSetDs.query();
  }

  function openModal(type:any) {
    Modal.open({
      ...modalProps[type],
      children: <Sider type={type} onOk={handleSave} psSetDs={psSetDs} />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: "base-lang-sider",
    });
  }

  function renderTime({ value }:any) {
    return isNil(value) ? '' : <TimePopover content={value} />;
  }

  function handleDelete() {
    const record = psSetDs.current;
    const mProps = {
      title: formatMessage({ id: `${intlPrefix}.permission.delete.title` }),
      children: formatMessage({ id: `${intlPrefix}.permission.delete.des` }),
      okText: formatMessage({ id: 'delete' }),
    };
    psSetDs.delete(record, mProps);
  }


  async function handleAsync() {
    const record = psSetDs.current;
    try {
      const res = await CodeManagerApis.asyncUser(organizationId, projectId, record.get('repositoryId'), record.get('id'))
      if (res?.failed) {
        Choerodon.prompt(res.message);
      }
      Choerodon.prompt(formatMessage({ id: `${intlPrefixPSset}.message.asyncSuccess` }));
      psSetDs.query();
    } catch (error) {
      Choerodon.handleResponseError(error);
    }
  }

  function renderAction({ record }:any) {
    // 权限层级大于50，不允许编辑和删除
    if (record.get('glAccessLevel') && Number(record.get('glAccessLevel').substring(1)) >= 50) {
      return null;
    }
    const actionData = [{
      service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
      text: formatMessage({ id: 'delete' }),
      action: handleDelete,
    }];
    const asyncData = [{
      service: ['choerodon.code.project.infra.code-lib-management.ps.project-owner'],
      text: <Tooltip title={formatMessage({ id: `${intlPrefixPSset}.message.asyncTips` })}>{formatMessage({ id: 'async' })}</Tooltip>,
      action: handleAsync,
    }];
    // 分支如果是未同步，不允许编辑和删除
    if (!record.get('syncGitlabFlag')) {
      return <Action data={asyncData} />;
    }
    return <Action data={actionData} />;
  }

  return (
    <Table
      dataSet={psSetDs}
      filter={(record:any)=> record.status !== "add"}
      queryBar={"bar" as TableQueryBarType}
      queryFieldsLimit={3}
      className="c7n-infra-code-management-table"
    >
      <Column name="realName" renderer={renderName} width={200} />
      <Column renderer={renderAction} width={70} />
      <Column name="loginName" renderer={renderLoginName} />
      <Column name="repositoryName" renderer={renderServiceName} />
      <Column name="roleNames" renderer={renderRole} />
      <Column name="glAccessLevelList" renderer={renderLevel} />
      <Column name="glExpiresAt" />
      <Column
        name="createdByName"
        renderer={({ record }:any) => (
          <div style={{ display: "inline-flex" }}>
            <UserAvatar
            // @ts-expect-error
              user={{
                id: record.get("createdUser").userId,
                loginName: record.get("createdUser").loginName,
                realName: record.get("createdUser").realName,
                imageUrl: record.get("createdUser").imageUrl,
                email: record.get("createdUser").email,
              }}
            />
          </div>
        )}
      />
      <Column name="lastUpdateDate" renderer={renderTime} />
    </Table>
  );
};

export default observer(PSsetTable);
