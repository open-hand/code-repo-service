import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { Page, Content, Breadcrumb, Header } from '@choerodon/boot';
import { useApprovalStore } from './stores';
import TodoList from './TodoList';
import DoneList from './DoneList';
import './index.less';

const { TabPane } = Tabs;

const intlPrefix = 'infra.env.approval';

export const TabKeyEnum = {
  TODO: 'todo',
  DONE: 'done',
};

function ClusterApproval() {
  const {
    formatMessage,
    permissions,
    todoListDs,
    doneListDs,
  } = useApprovalStore();

  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.TODO);

  const refresh = () => {
    if (activeTabKey === TabKeyEnum.TODO) {
      todoListDs.query();
    } else {
      doneListDs.query();
    }
  };

  const todoListProps = { todoListDs, formatMessage, activeTabKey };
  const doneListProps = { doneListDs, formatMessage, activeTabKey };

  return (
    <Page service={permissions}>
      <Header>
        <Button
          onClick={() => refresh()}
          icon="refresh"
        >
          {formatMessage({ id: 'refresh' })}
        </Button>
      </Header>
      <Breadcrumb />
      <Content className="cluster-approval-content">
        <Tabs
          activeKey={activeTabKey}
          animated={false}
          onChange={newActiveKey => setActiveTabKey(newActiveKey)}
          className="cluster-approval-content-tabs"
        >
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.todo`, defaultMessage: '我的待办' })} key={TabKeyEnum.TODO}>
            <TodoList {...todoListProps} />
          </TabPane>
          <TabPane tab={formatMessage({ id: `${intlPrefix}.view.done`, defaultMessage: '我的已办' })} key={TabKeyEnum.DONE}>
            <DoneList {...doneListProps} />
          </TabPane>
        </Tabs>
      </Content>
    </Page>
  );
}

export default observer(ClusterApproval);
