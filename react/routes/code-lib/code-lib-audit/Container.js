/* eslint-disable import/first */
/**
 * 代码库-组织层
 * @author JZH <zhihao.jiang@hand-china.com>
 * @creationDate 2020/3/26
 * @copyright 2020 ® HAND
 */
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { Page, Content, Header, Breadcrumb, HeaderButtons } from '@choerodon/boot';
import { Tabs } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Tips from '@/components/new-tips';
import { useStore, TabKeyEnum } from './stores';
import ProjectList from './ProjectList';
import TimeLine from './TimeLine';
import PSView from './PSView';
import { useModal } from 'choerodon-ui/pro';
import ExportAuthority from './export-authority-codelib';
import PsAudit from './ps-audit';
import './index.less';


const { TabPane } = Tabs;

const Container = () => {
  const [activeTabKey, setActiveTabKey] = useState(TabKeyEnum.PSVIEW);
  const [activeProject, setActiveProject] = useState({});
  const {
    optLogDs,
    psViewDs,
    projectListDs,
    timeLineStore,
    psAuditDs,
    organizationId,
    formatClient,
  } = useStore();
  const Modal = useModal();
  const refresh = () => {
    projectListDs.query().then(() => {
      setActiveProject({
        id: 'all',
        name: formatClient({ id: 'permission.allProject' }),
      });
    });
  };

  useEffect(() => {
    refresh();
  }, []);

  const onClickProject = (oProject) => {
    setActiveProject(oProject);
  };

  const projectListProps = useMemo(() => ({
    projectListDs, activeProject, onClickProject,
  }), [projectListDs, activeProject]);
  const psViewProps = useMemo(() => ({
    psViewDs, activeProject, activeTabKey,
  }), [activeProject, psViewDs, activeTabKey]);
  const optLogProps = useMemo(() => ({
    optLogDs, timeLineStore, activeProject, activeTabKey,
  }), [activeProject, optLogDs, timeLineStore, activeTabKey]);
  const exportAuthorityProps = useMemo(() => ({
    activeProject, psViewDs,
  }), [activeProject, psViewDs]);

  const psAuditProps = useMemo(() => ({
    psAuditDs, activeProject, activeTabKey, organizationId,
  }), [activeProject, psAuditDs, activeTabKey, organizationId]);

  const openExportModal = useCallback(() => {
    Modal.open({
      title: '权限导出确认',
      children: <ExportAuthority {...exportAuthorityProps} />,
    });
  }, []);

  return (
    <Page
      service={['choerodon.code.organization.infra.code-lib-management.ps.project-owner']}
      className="code-lib-audit"
    >
      <Header>
        <HeaderButtons
          showClassName={false}
          items={([{
            name: formatClient({ id: 'permission.permissionToExport' }),
            icon: 'get_app-o',
            display: true,
            handler: () => openExportModal(),
          }])}
        />
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
            <TabPane tab={formatClient({ id: 'permission.permission' })} key={TabKeyEnum.PSVIEW}>
              <PSView {...psViewProps} />
            </TabPane>
            <TabPane
              tab={
                <Tips
                  helpText={formatClient({ id: 'audit.tips' })}
                  title={formatClient({ id: 'permission.permission' })}
                />}
              key={TabKeyEnum.PSAUDIT}
            >
              <PsAudit {...psAuditProps} />
            </TabPane>
            <TabPane tab={formatClient({ id: 'log.log' })} key={TabKeyEnum.OPTLOG}>
              <TimeLine {...optLogProps} />
            </TabPane>
          </Tabs>
        </div>
      </Content>
    </Page>
  );
};

export default observer(Container);
