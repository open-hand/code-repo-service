import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import { handlePromptError } from '@/utils';
import OpsProjectListDS from './OpsProjectListDS';
import FormDS from './FormDS';
import ApplicationListDS from './ApplicationListDS';
import PlatformServiceDS from './PlatformServiceDS';
import InfrastructureDS from './InfrastructureDS';
import useStore from './useStore';

const Store = createContext();

export function useCreateApplyStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState,
    intl: { formatMessage },
    intlPrefix,
    id,
  } = props;
  const { currentMenuType: { projectId, organizationId }, userInfo: { realName } } = AppState;

  const createStore = useStore();

  // 运维项目
  const opsProjectList = useMemo(() => new DataSet(OpsProjectListDS({ organizationId, projectId })), [organizationId, projectId]);
  // 基础设施
  const infrastructureDs = useMemo(() => new DataSet(InfrastructureDS({ formatMessage, intlPrefix })), [projectId]);
  // 平台服务
  const platformServiceDs = useMemo(() => new DataSet(PlatformServiceDS({ formatMessage, intlPrefix })), [projectId]);
  // 申请组件清单
  const applicationListDs = useMemo(() => new DataSet(ApplicationListDS({ formatMessage, intlPrefix, infrastructureDs, platformServiceDs })), [infrastructureDs, platformServiceDs, projectId]);
  const formDs = useMemo(() => new DataSet(FormDS({ formatMessage, intlPrefix, applicationListDs, organizationId, projectId, realName, opsProjectList, id })), [opsProjectList, organizationId, projectId, id]);

  async function loadInitData() {
    applicationListDs.reset();
    const [formsData] = await axios.all([formDs.query()]);
    if (handlePromptError(formsData)) {
      const { applicationList } = formsData;
      if (applicationList) {
        applicationListDs.create(applicationList);
      }
    }
  }

  useEffect(() => {
    if (id) {
      loadInitData();
    } else {
      formDs.create();
      applicationListDs.create();
      infrastructureDs.create();
      platformServiceDs.create();
    }
  }, []);

  const value = {
    ...props,
    intlPrefix,
    AppState,
    formDs,
    applicationListDs,
    opsProjectList,
    infrastructureDs,
    platformServiceDs,
    createStore,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

