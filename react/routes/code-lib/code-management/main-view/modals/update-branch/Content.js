import React from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Select, Tooltip } from 'choerodon-ui/pro';
import './index.less';

export default observer((props) => {
  const { modal, branchFormDs, onOk } = props;
  async function handleOk() {
    if (branchFormDs.current && !branchFormDs.current.dirty && !branchFormDs.current.get('dirty')) {
      return true;
    }
    if (await branchFormDs.submit()) {
      onOk();
      branchFormDs.reset();
      return true;
    } else {
      return false;
    }
  }
  
  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    branchFormDs.reset();
  });

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
    return <span className={`ps-branch-update-icon icon-${type}`}>{type.slice(0, 1).toUpperCase()}</span>;
  };
  const rendererOpt = ({ text }) => (
    <div style={{ width: '100%', color: 'rgba(0, 0, 0, 0.2)' }}>
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
          name="name"
          disabled
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
