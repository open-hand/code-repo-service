import React, { useEffect } from "react";
import { useFormatMessage } from "@choerodon/master";
import { Form, Select } from "choerodon-ui/pro";

import {
  LabelLayoutType,
  LabelAlignType,
} from "choerodon-ui/pro/lib/form/Form";
import map from "lodash/map";
import { usPsManagerStore } from "../stores";
import "./index.less";
import { observer } from "mobx-react-lite";
import { Tooltip } from 'choerodon-ui';

const { Option } = Select;

const prefixCls = "c7ncd-codeAppSelector";

const AppSelector = (props: any) => {
  const { showAllOption = false } = props;
  const { branchServiceDs, branchAppId, setBranchApp } = usPsManagerStore();

  const format = useFormatMessage('c7ncd.codeLibManagement');

  function handleSelect(value: any) {
    setBranchApp(value.repositoryId);
  }

  function renderOpts() {
    const res = map(
      branchServiceDs.toData(),
      ({ repositoryId, repositoryName, repositoryCode, externalConfigId }) => (
        <Option value={repositoryId} key={repositoryId} disabled={Boolean(externalConfigId)}>
          <Tooltip title={externalConfigId ? '外置GitLab代码仓库的应用服务不支持配置' : ''}>
            {`${repositoryName}(${repositoryCode})`}
          </Tooltip>

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

  useEffect(() => {
    branchServiceDs.current?.set('repositoryIds', 'all');
    setBranchApp('all')
  }, []);

  const renderOption = ({ record, value }: any) => {
    const externalConfigId = record?.get('externalConfigId')
    const repositoryName = record?.get('repositoryName')
    const repositoryCode = record?.get('repositoryCode')
    return (
      <Tooltip
        title={externalConfigId ? "外置GitLab代码仓库的应用服务不支持配置" : ""}
      >
        {`${repositoryName}${value !== 'all' ? (repositoryCode) : ''}`}
      </Tooltip>
    );
  };

  const onOption = ({ record }: any) => {
    const externalConfigId = record?.get('externalConfigId')
    return ({
      disabled: Boolean(externalConfigId),
    });
  };

  const optionsFilter = (record:any)=> {
    let flag = true
    if(record?.get('repositoryId') === 'all' && !showAllOption) {
      flag =  false
    }
    return flag
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
        // value={branchAppId}
        onChange={handleSelect}
        colSpan={3}
        prefix={`${format({ id: 'ApplicationService' })}:`}
        className={`${prefixCls}-select`}
        optionRenderer={renderOption}
        onOption={onOption}
        optionsFilter={optionsFilter}
      >
        {/* {renderOpts()} */}
      </Select>
    </Form>
  );
};

export default observer(AppSelector);