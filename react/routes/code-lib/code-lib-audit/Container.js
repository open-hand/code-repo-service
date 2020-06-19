/**
 * 代码库-项目层
 * @author JZH <zhihao.jiang@hand-china.com>
 * @creationDate 2020/3/26
 * @copyright 2020 ® HAND
 */
import React, { useEffect, useMemo, useState } from 'react';
import { Page, Content, Header, Breadcrumb } from '@choerodon/boot';
import { Tabs, Button } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Tips from '@/components/new-tips';
import { useStore, TabKeyEnum } from './stores';
import ProjectList from './ProjectList';
import TimeLine from './TimeLine';
import PSView from './PSView';
import ExportAuthority from './export-authority-codelib';
import PsAudit from './ps-audit';
import './index.less';

const { TabPane } = Tabs;

const Container = () => {
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.PSVIEW);
  const [activeProject, setActiveProject] = useState({});
  const [exportModalVisible, setExportModalVisible] = useState(false);
  const {
    intlPrefix,
    intl: { formatMessage },
    optLogDs,
    psViewDs,
    projectListDs,
    timeLineStore,
    psAuditDs,
    organizationId,
  } = useStore();

  const refresh = () => {
    projectListDs.query().then(() => {
      setActiveProject({
        id: 'all',
        name: formatMessage({ id: `${intlPrefix}.view.allProject`, defaultMessage: '所有项目' }),
      });
    });
  };

  useEffect(() => {
    refresh();
  }, []);

  const onClickProject = (oProject) => {
    setActiveProject(oProject);
  };

  const projectListProps = useMemo(() => ({ formatMessage, projectListDs, activeProject, onClickProject }), [formatMessage, projectListDs, activeProject]);
  const psViewProps = useMemo(() => ({ psViewDs, activeProject, formatMessage, activeTabKey }), [activeProject, psViewDs, formatMessage, activeTabKey]);
  const optLogProps = useMemo(() => ({ formatMessage, optLogDs, timeLineStore, activeProject, activeTabKey }), [activeProject, optLogDs, timeLineStore, activeTabKey]);
  const exportAuthorityProps = useMemo(() => ({ formatMessage, exportModalVisible, setExportModalVisible, activeProject, psViewDs }), [exportModalVisible, activeProject, psViewDs]);
  const psAuditProps = useMemo(() => ({ psAuditDs, activeProject, formatMessage, activeTabKey, organizationId }), [activeProject, psAuditDs, formatMessage, activeTabKey, organizationId]);

  return (
    <Page
      service={['choerodon.code.organization.infra.code-lib-management.ps.project-owner']}
      className="code-lib-audit"
    >
      <Header >
        <Button
          className="leftBtn"
          icon="get_app"
          funcType="flat"
          onClick={() => setExportModalVisible(true)}
        >
          {formatMessage({ id: 'exportAuth', defaultMessage: '导出权限' })}
        </Button>
      </Header>
      <Breadcrumb />
      <Content>
        <ProjectList {...projectListProps} />

        <div className="code-lib-audit-panel-group">
          <div className="panel-group-title">
            {activeProject.name}
          </div>
          <Tabs
            defaultActiveKey={activeTabKey}
            className="code-lib-audit-tabs"
            animated={false}
            onChange={newActiveKey => setActiveTabKey(newActiveKey)}
          >
            <TabPane tab={formatMessage({ id: `${intlPrefix}.view.psView` })} key={TabKeyEnum.PSVIEW}>
              <PSView {...psViewProps} />
            </TabPane>
            <TabPane
              tab={<Tips
                helpText={formatMessage({ id: 'infra.codeManage.ps.message.psAudit.tips' })}
                title={formatMessage({ id: 'infra.codeManage.ps.message.psAudit' })}
              />}
              key={TabKeyEnum.PSAUDIT}
            >
              <PsAudit {...psAuditProps} />
            </TabPane>
            <TabPane tab={formatMessage({ id: `${intlPrefix}.view.optLog`, defaultMessage: '操作日志' })} key={TabKeyEnum.OPTLOG}>
              <TimeLine {...optLogProps} />
            </TabPane>
          </Tabs>
        </div>
      </Content>
      <ExportAuthority {...exportAuthorityProps} />
    </Page>
  );
};

export default observer(Container);
