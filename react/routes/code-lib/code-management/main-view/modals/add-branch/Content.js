import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, Tooltip } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import { useAddBranchStore } from './stores';
import './index.less';

export default observer(() => {
  const {
    branchFormDs,
    modal,
    refresh,
  } = useAddBranchStore();
  modal.handleOk(async () => {
    try {
      if (await branchFormDs.submit() !== false) {
        refresh();
        return true;
      } else {
        return false;
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  });
  
  modal.handleCancel(() => {
    branchFormDs.reset();
  });

  function handleChange(value) {
    branchFormDs.current.set('branchName', value);
  }

  /**
   * 获取列表的icon
   * @param name 分支名称
   * @returns {*}
   */
  const getIcon = (name) => {
    const nameArr = ['feature', 'release', 'bugfix', 'hotfix'];
    let type = '';
    if (name.includes('-') && nameArr.includes(name.split('-')[0])) {
      // eslint-disable-next-line
      type = name.split('-')[0];
    } else if (name === 'master') {
      type = name;
    } else {
      type = 'custom';
    }
    return <span className={`ps-branch-add-icon icon-${type}`}>{type.slice(0, 1).toUpperCase()}</span>;
  };
  const rendererOpt = ({ text }) => (
    <div style={{ width: '100%' }}>
      {text ? getIcon(text) : ''} {text}
    </div>
  );

  const optionRenderer = ({ text }) => (
    <Tooltip title={text} placement="left">
      {rendererOpt({ text })}
    </Tooltip>
  );

  return (
    <div>
      <Form dataSet={branchFormDs}>
        <Select
          name="branchName"
          combo
          onChange={handleChange}
          clearButton={false}
          optionRenderer={optionRenderer}
          renderer={rendererOpt}
        />
        <Select name="mergeAccessLevel" clearButton={false} />
        <Select name="pushAccessLevel" clearButton={false} />
      </Form>
    </div>
  );
});
