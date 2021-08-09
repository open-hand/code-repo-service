import React from "react";
import { Form, Select } from "choerodon-ui/pro";

import {
  LabelLayoutType,
  LabelAlignType,
} from "choerodon-ui/pro/lib/form/Form";
import map from "lodash/map";
import { usPsManagerStore } from "../stores";
import "./index.less";
import { observer } from "mobx-react-lite";

const { Option } = Select;

const prefixCls = "c7ncd-codeAppSelector";

const AppSelector = (props: any) => {
  const { showAllOption = false } = props;
  const { branchServiceDs, branchAppId, setBranchApp } = usPsManagerStore();

  function handleSelect(value: string) {
    setBranchApp(value);
  }

  function renderOpts() {
    const res = map(
      branchServiceDs.toData(),
      ({ repositoryId, repositoryName }) => (
        <Option value={repositoryId} key={repositoryId}>
          {repositoryName}
        </Option>
      )
    );
    if (showAllOption) {
      res.unshift(
        <Option value="all" key="all">
          全部应用服务
        </Option>
      );
    }
    return res;
  }

  return (
    <Form
      columns={3}
      className={prefixCls}
      labelLayout={"horizontal" as LabelLayoutType}
    >
      <Select
        style={{ width: "100%" }}
        searchable
        clearButton={false}
        dataSet={branchServiceDs}
        name="repositoryIds"
        value={branchAppId}
        onChange={handleSelect}
        colSpan={3}
        prefix="应用服务："
        className={`${prefixCls}-select`}
      >
        {renderOpts()}
      </Select>
    </Form>
  );
};

export default observer(AppSelector);