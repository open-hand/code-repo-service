/**
 * 权限分配
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
import React, { useEffect } from "react";
import { Page, Action } from "@choerodon/boot";
import { Table, Modal, Icon, Tooltip } from "choerodon-ui/pro";
import { observer } from "mobx-react-lite";
import { StatusTag } from "@choerodon/components";
import ApplyDetail from "../../../modals/apply-detail";
import { usPsManagerStore } from "../../../stores";
import { TableQueryBarType } from "choerodon-ui/pro/lib/table/enum";

const { Column } = Table;
const modalKey = Modal.key();

const ApplyView = observer(() => {
  const {
    intl: { formatMessage },
    applyViewDs,
  } = usPsManagerStore();

  function handleTableFilter(record: any) {
    return record.status !== "add";
  }

  function renderStatus(record: any) {
    const res = record.value.toLowerCase();
    const error = record.record.get("approvalMessage");
    return (
      <div style={{ display: "flex", alignItems: "center" }}>
        <StatusTag
          type="default"
          colorCode={record.value.toUpperCase()}
          name={formatMessage({ id: `infra.approval.${res}` })}
          style={{ lineHeight: "16px", width: "42px" }}
        />
        {error && (
          <Tooltip title={error}>
            <Icon
              style={{
                color: "rgb(247, 103, 118)",
                marginLeft: "3px",
              }}
              type="info"
            />
          </Tooltip>
        )}
      </div>
    );
  }

  function handleOpenModal(record: any) {
    applyViewDs.current = record;
    const isApproval = record.getPristineValue("approvalState") === "PENDING";
    Modal.open({
      key: modalKey,
      drawer: true,
      title: formatMessage({ id: "infra.codeManage.ps.message.apply.detail" }),
      style: { width: 380 },
      className: "code-lib-ps-approval-sider",
      children: (
        <ApplyDetail
          //@ts-expect-error
          formatMessage={formatMessage}
          record={record}
          psApprovalDs={applyViewDs}
        />
      ),
      okCancel: isApproval,
      fullScreen: true,
      okText: isApproval
        ? formatMessage({ id: "commit" })
        : formatMessage({ id: "close" }),
    });
  }

  function renderAction({ record }: any) {
    const actionDatas = [];
    if (record.get("approvalState") !== "PENDING") {
      actionDatas.push({
        service: [
          "choerodon.code.project.infra.code-lib-management.ps.project-owner",
        ],
        text: formatMessage({ id: "view.detail", defaultMessage: "查看详情" }),
        action: () => handleOpenModal(record),
      });
    } else {
      return null;
    }
    return <Action data={actionDatas} />;
  }

  return (
    <Table
      className="c7n-infra-code-management-table"
      dataSet={applyViewDs}
      filter={handleTableFilter}
      queryBar={"bar" as TableQueryBarType}
      queryFieldsLimit={3}
    >
      <Column header={formatMessage({ id: 'c7ncd.codeLibManagement.ApplicationService' })} name="repositoryName" width={250} />
      <Column renderer={renderAction} width={70} />
      <Column header={formatMessage({ id: 'c7ncd.codeLibManagement.Type' })} name="applicantType" />
      <Column header={formatMessage({ id: 'c7ncd.codeLibManagement.PermissionType' })} name="accessLevel" />
      <Column header={formatMessage({ id: 'c7ncd.codeLibManagement.Status' })} name="approvalState" renderer={renderStatus} />
      <Column header={formatMessage({ id: 'c7ncd.codeLibManagement.ApplicationDate' })} name="applicantDate" />
    </Table>
  );
});

export default ApplyView;
