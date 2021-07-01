import React, { useEffect } from "react";
import { Page, Action } from "@choerodon/boot";
import { Table, Modal, Icon, Tooltip } from "choerodon-ui/pro";
import { observer } from "mobx-react-lite";
import { StatusTag } from "@choerodon/components";
import UserAvatar from "@/components/user-avatar";
import Sider from "../../../modals/ps-approval";
import { TableQueryBarType } from "choerodon-ui/pro/lib/table/enum";
import { usPsManagerStore } from "../../../stores";

const { Column } = Table;
const intlPrefix = "infra.codeManage.ps";

const modalKey = Modal.key();

const PsApproval = observer(() => {
  const {
    intl: { formatMessage },
    psApprovalDs,
    psSetDs,
    overStores,
    organizationId,
    projectId,
  } = usPsManagerStore();

  useEffect(()=>{
    psApprovalDs.query();
  }, []);

  function handleTableFilter(record: any) {
    return record.status !== "add";
  }

  function handleOpenModal(record: any) {
    psApprovalDs.current = record;
    const isApproval = record.getPristineValue("approvalState") === "PENDING";
    Modal.open({
      key: modalKey,
      drawer: true,
      title: isApproval
        ? formatMessage(
            { id: `${intlPrefix}.message.approveDrawerTitle` },
            { name: record.get("applicantUser").realName }
          )
        : formatMessage(
            { id: `${intlPrefix}.message.approveDetail` },
            { name: record.get("applicantUser").realName }
          ),
      style: { width: 380 },
      className: "code-lib-ps-approval-sider",
      children: (
        <Sider
          // @ts-expect-error
          formatMessage={formatMessage}
          record={record}
          psApprovalDs={psApprovalDs}
          overStores={overStores}
          organizationId={organizationId}
          projectId={projectId}
          psSetDs={psSetDs}
        />
      ),
      okCancel: isApproval,
      fullScreen: true,
      okText: isApproval
        ? formatMessage({ id: "commit" })
        : formatMessage({ id: "close" }),
    });
  }

  function renderName({ record }: any) {
    const avatar = (
      <UserAvatar
        // @ts-expect-error
        user={{
          id: record.get("applicantUser").userId,
          loginName: record.get("applicantUser").loginName,
          realName: record.get("applicantUser").realName,
          imageUrl: record.get("applicantUser").imageUrl,
          email: record.get("applicantUser").email,
        }}
        hiddenText
      />
    );

    return (
      <div style={{ display: "inline-flex" }}>
        {record.get("approvalState") === "PENDING" ? (
          <React.Fragment>
            {avatar}
            <span className="c7n-infra-code-management-user-head-name">
              {record.get("applicantUser").realName}
            </span>
          </React.Fragment>
        ) : (
          <React.Fragment>
            {avatar}
            <span
              className="c7n-infra-code-management-table-name"
              onClick={() => handleOpenModal(record)}
            >
              {record.get("applicantUser").realName}
            </span>
          </React.Fragment>
        )}
      </div>
    );
  }

  function renderAction({ record }: any) {
    const actionDatas = [];
    if (record.get("approvalState") === "PENDING") {
      actionDatas.push({
        service: [
          "choerodon.code.project.infra.code-lib-management.ps.project-owner",
        ],
        text: formatMessage({ id: "approve" }),
        action: () => handleOpenModal(record),
      });
    } else {
      return null;
    }
    return <Action data={actionDatas} />;
  }

  function renderStatus(record: any) {
    const res = record.value.toLowerCase();
    const approvalMessage = record.record.get("approvalMessage");
    return (
      <>
        <StatusTag
          type="default"
          colorCode={record.value.toUpperCase()}
          name={formatMessage({ id: `infra.approval.${res}` })}
        />
        {approvalMessage && (
          <Tooltip title={approvalMessage}>
            <Icon type="info" style={{ marginLeft: "5px", color: "#F76776" }} />
          </Tooltip>
        )}
      </>
    );
  }

  return (
    <Table
      dataSet={psApprovalDs}
      filter={handleTableFilter}
      queryBar={"bar" as TableQueryBarType}
      queryFieldsLimit={3}
      className="c7n-infra-code-management-table"
    >
      <Column name="applicantUser" renderer={renderName} width={200} />
      <Column renderer={renderAction} width={70} />
      <Column name="approvalState" renderer={renderStatus} />
      <Column name="repositoryName" />
      <Column name="applicantType" />
      <Column name="accessLevel" />
      <Column name="applicantDate" />
    </Table>
  );
});

export default PsApproval;
