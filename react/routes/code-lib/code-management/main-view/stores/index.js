import React, { createContext, useContext, useMemo, useEffect, useState } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { useCheckPermission } from '@/utils/index';
import PsSetDataSet from './PsSetDataSet';
import BranchDataSet from './BranchDataSet';
import TagDataSet from './TagDataSet';
import PsOverViewDataSet from './PsOverViewDataSet';
// import usePermissionStore from '../modals/stores/useStore';
// import PsManagerDataSet from './PsManagerDataSet';
import BranchServiceDs from './BranchServiceDataSet';
import ListViewDataSet from './ListViewDataSet';
import useStore from './useStore';
import PsApprovalDS from './PsApprovalDS';
import PsAuditDS from './PsAuditDS';
import SecurityAuditDS from './SecurityAuditDS';
import ApplyViewDS from './ApplyViewDS';

const Store = createContext();

export function usPsManagerStore() {
  return useContext(Store);
}


export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    AppState: { currentMenuType: { projectId, organizationId } },
    intl: { formatMessage },
    children,
  } = props;

  const [appId, setApp] = useState(undefined);
  const [branchAppId, setBranchApp] = useState(undefined);

  const hasPermission = useCheckPermission(['choerodon.code.project.infra.code-lib-management.ps.project-owner']);
  const hasMemberPermission = useCheckPermission(['choerodon.code.project.infra.code-lib-management.ps.project-member']);


  const intlPrefix = 'infra';
  const branchServiceDs = useMemo(() => new DataSet(BranchServiceDs(formatMessage)), [projectId]); // 分支/标签的应用服务下拉框数据
  const psSetDs = useMemo(() => new DataSet(PsSetDataSet(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);
  const branchDs = useMemo(() => new DataSet(BranchDataSet(formatMessage, projectId, branchAppId)), [formatMessage, projectId, branchAppId]);
  const tagDs = useMemo(() => new DataSet(TagDataSet(formatMessage, projectId, branchAppId)), [formatMessage, projectId, branchAppId]);
  const psOverViewDs = useMemo(() => new DataSet(PsOverViewDataSet(formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);

  const listViewDs = useMemo(() => new DataSet(ListViewDataSet(formatMessage, organizationId, projectId)), [formatMessage, projectId]); // 操作日志
  const overStores = useStore();

  const psApprovalDs = useMemo(() => new DataSet(PsApprovalDS(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);
  const [executionDate, setExecutionDate] = useState(undefined);
  const psAuditDs = useMemo(() => new DataSet(PsAuditDS(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);
  const securityAuditDs = useMemo(() => new DataSet(SecurityAuditDS(intlPrefix, formatMessage, organizationId, projectId)), [formatMessage, projectId]);
  const applyViewDs = useMemo(() => new DataSet(ApplyViewDS(intlPrefix, formatMessage, organizationId, projectId, branchServiceDs)), [formatMessage, projectId, branchServiceDs]);

  useEffect(() => {
    branchServiceDs.transport.read = {
      url: `/rducm/v1/organizations/${organizationId}/projects/${projectId}/gitlab/repositories/list-by-active`,
      method: 'get',
    };
    branchServiceDs.query().then((res) => {
      branchServiceDs.current.set('repositoryIds', res[0].repositoryId);
      setBranchApp(res[0].repositoryId);
    });
  }, [projectId]);


  const value = {
    ...props,
    branchServiceDs,
    psSetDs,
    branchDs,
    tagDs,
    psOverViewDs,
    params: {
      projectId,
    },
    prefixCls: 'code-management',
    intlPrefix,
    appId,
    setApp,
    branchAppId,
    setBranchApp,
    listViewDs,
    overStores,
    psApprovalDs,
    organizationId,
    projectId,
    executionDate,
    setExecutionDate,
    psAuditDs,
    securityAuditDs,
    hasPermission,
    hasMemberPermission,
    applyViewDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
