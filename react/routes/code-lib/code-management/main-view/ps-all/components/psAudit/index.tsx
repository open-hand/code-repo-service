import React, { useEffect } from "react";
import { Page, Action, Choerodon } from "@choerodon/boot";
import { Table, Modal } from "choerodon-ui/pro";
import { Tooltip, Row, Col, Icon, message } from "choerodon-ui";
import { observer } from "mobx-react-lite";
import { isNil } from "lodash";
import TimeAgo from "timeago-react";
import moment from "moment";
import UserAvatar from "@/components/user-avatar";
import "./index.less";
import { usPsManagerStore } from "../../../stores";
import { TableQueryBarType } from "choerodon-ui/pro/lib/table/enum";
import Apis from '../../../../apis';

const { Column } = Table;
const intlPrefix = "infra.codeManage.ps";

const modalKey = Modal.key();

const PsAudit = observer(() => {
  const {
    intl: { formatMessage },
    psAuditDs,
    appId,
    AppState: {
      currentMenuType: { id: projectId, organizationId },
    },
    overStores,
    executionDate,
    setExecutionDate,
  } = usPsManagerStore();

  function refresh() {
    psAuditDs.query();
  }

  async function fetchExecutionDate() {
    try {
      const res = Apis.fetchExecutionDate(organizationId, projectId);
      if (res.failed) {
        Choerodon.prompt(res.message);
        return false;
      } else {
        const dataStr = res.auditEndDate
          ? moment(res.auditEndDate).format("YYYY-MM-DD HH:mm:ss")
          : undefined;
        setExecutionDate(dataStr);
        return true;
      }
    } catch (error) {
      Choerodon.handleResponseError(error);
        return false;
    }
  }

  useEffect(() => {
    refresh();
    fetchExecutionDate();
  }, [appId]);

  async function handleOk(record:any) {
    const params = {
      organizationId,
      projectId,
      id: record.get("id"),
      repositoryId: record.get("repositoryId"),
    };
    try {
      const res = await Apis.asyncPermission(params)
      if (res.failed) {
        message.error(res.message);
        return false;
      } else {
        refresh();
        message.success(
          formatMessage({ id: "infra.codeManage.ps.message.asyncSuccess" })
        );
        return true;
      }
    } catch (error) {
      Choerodon.handleResponseError(error);
      return false;
    }
  }

  const openDelete = (record:any) => {
    Modal.open({
      key: modalKey,
      title: formatMessage({ id: `${intlPrefix}.operate.fixPs` }),
      children: formatMessage({ id: `${intlPrefix}.operate.fixPs.confirm` }),
      onOk: () => handleOk(record),
      movable: false,
    });
  };

  function renderAction({ record }:any) {
    const actionData = [
      {
        service: [
          "choerodon.code.project.infra.code-lib-management.ps.project-owner",
        ],
        text: formatMessage({ id: "fix" }),
        action: () => openDelete(record),
      },
    ];
    return <Action data={actionData} />;
  }

  function handleTableFilter(record:any) {
    return record.status !== "add";
  }

  function renderTime() {
    const date = isNil(executionDate) ? (
      formatMessage({ id: "none" })
    ) : (
      <Tooltip title={executionDate}>
        <TimeAgo
          datetime={executionDate}
          locale={Choerodon.getMessage("zh_CN", "en")}
        />
      </Tooltip>
    );
    return date;
  }

  function renderName({ record }:any) {
    const avatar = (
      <UserAvatar
        // @ts-expect-error
        user={{
          id: record.get("user").userId,
          loginName: record.get("user").loginName,
          realName: record.get("user").realName,
          imageUrl: record.get("user").imageUrl,
          email: record.get("user").email,
        }}
      />
    );
    return <div style={{ display: "inline-flex" }}>{avatar}</div>;
  }
  function renderLoginName({ record }:any) {
    return <span>{record.get("user").loginName}</span>;
  }
  function renderLevel({ text, record }:any) {
    return (
      <span
        style={{ color: !record.get("accessLevelSyncFlag") ? "#EF4E42" : "" }}
      >
        {text}
      </span>
    );
  }
  function renderDate({ text, record }:any) {
    return (
      <span
        style={{ color: !record.get("expiresAtSyncFlag") ? "#EF4E42" : "" }}
      >
        {text || "-"}
      </span>
    );
  }

  return (
    <Table
      dataSet={psAuditDs}
      filter={handleTableFilter}
      queryBar={"bar" as TableQueryBarType}
      queryFieldsLimit={3}
      className="c7n-infra-code-management-table"
    >
      <Column name="realName" renderer={renderName} width={150} />
      <Column renderer={renderAction} width={70} />
      <Column name="loginName" renderer={renderLoginName} />
      <Column name="repositoryName" width={180} />
      <Column name="accessLevel" renderer={renderLevel} />
      <Column name="expiresAt" renderer={renderDate} />
      <Column name="glAccessLevel" renderer={renderLevel} />
      <Column name="glExpiresAt" renderer={renderDate} />
    </Table>
  );
});

export default PsAudit;
