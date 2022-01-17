/**
 * 分支/标签
 * @author LZY <zhuyan.luo@hand-china.com>
 * @creationDate 2020/02/21
 * @copyright 2020 ® HAND
 */
/* eslint-disable */
import React, { useEffect } from "react";
import { Page, Action } from "@choerodon/boot";
import { useFormatMessage } from "@choerodon/master";
import { Table, Modal, Form, Select } from "choerodon-ui/pro";
import { observer } from "mobx-react-lite";
import { map } from "lodash";
import { usPsManagerStore } from "../stores";
import UpdateBranch from "../modals/update-branch";
import UpdateTag from "../modals/update-tag";
import "../index.less";
import { Tooltip } from "choerodon-ui";

const { Column } = Table;
const { Option } = Select;
const intlPrefix = "infra.codeManage";

const modalKey = Modal.key();

const PsBranch = observer(() => {
  const {
    intl: { formatMessage },
    branchDs,
    tagDs,
    branchAppId,
    setBranchApp,
    hasPermission,
    branchServiceDs,
  } = usPsManagerStore();

  const format = useFormatMessage("c7ncd.codeLibManagement");

  function refreshBranch() {
    branchDs.query();
  }
  function refreshTag() {
    tagDs.query();
  }

  useEffect(() => {
    if (branchAppId && branchAppId!=='all') {
      refreshBranch();
      refreshTag();
    }
  }, [branchAppId]);

  function handleDelete(type) {
    if (type === "branch") {
      const record = branchDs.current;
      const modalProps = {
        title: formatMessage({ id: `${intlPrefix}.cancel.protected` }),
        children: formatMessage({
          id: `${intlPrefix}.cancel.protected.confirm.branch`,
        }),
        okText: formatMessage({ id: "ok" }),
      };
      branchDs.delete(record, modalProps);
    } else {
      const record = tagDs.current;
      const modalProps = {
        title: formatMessage({ id: `${intlPrefix}.cancel.protected` }),
        children: formatMessage({
          id: `${intlPrefix}.cancel.protected.confirm.tag`,
        }),
        okText: formatMessage({ id: "ok" }),
      };
      tagDs.delete(record, modalProps);
    }
  }

  function renderAction(type) {
    const actionData = [
      {
        service: [
          "choerodon.code.project.infra.code-lib-management.ps.project-owner",
        ],
        text: format({ id: "Unprotect" }),
        action: () => handleDelete(type),
      },
    ];
    return <Action data={actionData} />;
  }

  function handleTableFilter(record) {
    return record.status !== "add";
  }

  function openBranch() {
    Modal.open({
      title: formatMessage({ id: "infra.update.branch" }),
      children: <UpdateBranch onOk={refreshBranch} branchFormDs={branchDs} />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: "base-lang-sider",
      okText: formatMessage({ id: "save" }),
    });
  }
  function openTag() {
    Modal.open({
      title: formatMessage({ id: "infra.update.tag" }),
      children: <UpdateTag onOk={refreshTag} tagFormDs={tagDs} />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: "base-lang-sider",
      okText: formatMessage({ id: "save" }),
    });
  }
  /**
   * 获取列表的icon
   * @param name 分支名称
   * @returns {*}
   */
  function getIcon(name) {
    const nameArr = ["feature", "release", "bugfix", "hotfix"];
    let type = "";
    if (name.includes("-") && nameArr.includes(name.split("-")[0])) {
      // eslint-disable-next-line
      type = name.split("-")[0];
    } else if (name === "master") {
      type = name;
    } else {
      type = "custom";
    }
    return (
      <span className={`ps-branch-icon icon-${type}`}>
        {type.slice(0, 1).toUpperCase()}
      </span>
    );
  }

  function renderBranchName({ text }) {
    return (
      <div>
        {getIcon(text)}
        {hasPermission ? (
          <span
            onClick={() => openBranch()}
            className="c7n-infra-code-management-table-name"
          >
            {text}
          </span>
        ) : (
          <span>{text}</span>
        )}
      </div>
    );
  }
  function renderTagName({ text }) {
    const nameDom = hasPermission ? (
      <span
        onClick={() => openTag()}
        className="c7n-infra-code-management-table-name"
      >
        {text}
      </span>
    ) : (
      <span>{text}</span>
    );
    return nameDom;
  }

  function handleSelect(value) {
    setBranchApp(value.repositoryId);
  }

  const renderOption = ({ record }) => {
    const externalConfigId = record?.get('externalConfigId')
    const repositoryName = record?.get('repositoryName')
    return (
      <Tooltip
        title={externalConfigId ? "外置GitLab代码仓库的应用服务不支持配置" : ""}
      >
        {repositoryName}
      </Tooltip>
    );
  };

  const onOption = ({ record }) => {
    const externalConfigId = record?.get('externalConfigId')
    return ({
      disabled: Boolean(externalConfigId),
    });
  };

  const optionsFilter = (record)=> {
    let flag = true
    if(record?.get('repositoryId') === 'all') {
      flag =  false
    }
    return flag
  }

  return (
    <Page
      service={[
        "choerodon.code.project.infra.code-lib-management.ps.project-member",
        "choerodon.code.project.infra.code-lib-management.ps.project-owner",
      ]}
    >
      <div
        style={{ paddingTop: ".08rem", display: "flex", alignItems: "center" }}
      >
        <Form
          columns={3}
          style={{ width: "3.4rem", maxWidth: "3.8rem" }}
          dataSet={branchServiceDs}
        >
          <Select
            style={{ width: "100%" }}
            searchable
            clearButton={false}
            name="repositoryIds"
            value={branchAppId}
            onChange={handleSelect}
            colSpan={3}
            optionRenderer={renderOption}
            onOption= {onOption}
            optionsFilter={optionsFilter}
          >
            {/* {
              map(branchServiceDs.toData(), ({ repositoryId, repositoryName, externalConfigId }) => {
                return (
                  <Option
                    value={repositoryId}
                    key={repositoryId}
                    disabled={Boolean(externalConfigId)}
                  >
                    <Tooltip title={externalConfigId ? '外置GitLab代码仓库的应用服务不支持配置' : ''}>
                      {repositoryName}
                    </Tooltip>
                  </Option>)
              })
            } */}
          </Select>
        </Form>
      </div>
      <div>
        <div className="ps-branch-tips-title">
          {format({ id: "ProtectedBranchSetting" })}
        </div>
        <Table
          dataSet={branchDs}
          queryBar="none"
          filter={handleTableFilter}
          pagination={false}
        >
          <Column
            name="name"
            renderer={renderBranchName}
            width={315}
            help="受保护的分支可以设置允许往此分支上合并或推送代码的特定角色"
          />
          <Column renderer={() => renderAction("branch")} width={70} />
          <Column name="mergeAccessLevel" />
          <Column label="" name="pushAccessLevel" />
        </Table>
      </div>
      <div>
        <div className="ps-branch-tips-title" style={{ marginTop: "0.16rem" }}>
          {format({ id: "ProtectedTagSetting" })}
        </div>
        <Table
          dataSet={tagDs}
          queryBar="none"
          filter={handleTableFilter}
          pagination={false}
        >
          <Column
            name="name"
            renderer={renderTagName}
            width={315}
            help="受保护的标记可以设置允许创建或更新此标记的特定角色"
          />
          <Column renderer={() => renderAction("tag")} width={70} />
          <Column name="createAccessLevel" />
        </Table>
      </div>
    </Page>
  );
});

export default PsBranch;
